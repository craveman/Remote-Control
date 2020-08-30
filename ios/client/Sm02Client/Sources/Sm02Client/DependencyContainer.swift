
import NIO
import NIOExtras


protocol Singletons {

  var eventsManager: HandlersManager<ConnectionEvent> { get }
  var messagesManager: HandlersManager<Inbound> { get }
  var serversManager: HandlersManager<RemoteAddress> { get }
}

protocol NetworkServiceFactory {

  func makeTcpClient () -> Sm02TcpClient
  func makeUdpLookup () -> Sm02LookupServer
}

protocol ChannelHandlerFactory {

  var decoderChanneHandler: ChannelHandler { get }
  var encoderChanneHandler: ChannelHandler { get }
  var errorChannelHandler: ChannelHandler { get }

  func makeInboundMessagesHandler () -> ChannelHandler

  func makeLookupMessagesHandler () -> ChannelHandler

  func makeClientPipeline (_ channel: Channel) -> EventLoopFuture<Void>

  func makeLookupPipeline (_ channel: Channel) -> EventLoopFuture<Void>
}

class DependencyContainer: Singletons {

  lazy private(set) var eventsManager = HandlersManager<ConnectionEvent>()
  lazy private(set) var messagesManager = HandlersManager<Inbound>()
  lazy private(set) var serversManager = HandlersManager<RemoteAddress>()
}

extension DependencyContainer: NetworkServiceFactory {

  func makeTcpClient () -> Sm02TcpClient {
    return Sm02TcpClient(container: self)
  }

  func makeUdpLookup () -> Sm02LookupServer {
    return Sm02UdpLookupServer(container: self)
  }
}

extension DependencyContainer: ChannelHandlerFactory {

  private static var cache = Cache()

  var decoderChanneHandler: ChannelHandler {
    return DependencyContainer.cache.get(ByteBufferToInboundDecoder.self)
      .orBuildAndCache { ByteBufferToInboundDecoder() }
  }

  var encoderChanneHandler: ChannelHandler {
    return DependencyContainer.cache.get(OutboundToByteBufferEncoder.self)
      .orBuildAndCache { OutboundToByteBufferEncoder() }
  }

  var errorChannelHandler: ChannelHandler {
    return DependencyContainer.cache.get(LogOnErrorHandler.self)
      .orBuildAndCache { [weak self] in
        LogOnErrorHandler(container: self!)
      }
  }

  func makeInboundMessagesHandler () -> ChannelHandler {
    return InboundMessagesHandler(container: self)
  }

  func makeLookupMessagesHandler () -> ChannelHandler {
    return LookupMessagesHandler(container: self)
  }

  func makeClientPipeline (_ channel: Channel) -> EventLoopFuture<Void> {
    return channel.pipeline.addHandler(BackPressureHandler()).flatMap { [weak self] in
      let handlers = [
        ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
        LengthFieldPrepender(lengthFieldLength: .two),
        TickTockHandler(container: self!),
        self!.decoderChanneHandler,
        self!.encoderChanneHandler,
        self!.makeInboundMessagesHandler(),
        self!.errorChannelHandler,
      ]
      return channel.pipeline.addHandlers(handlers)
    }
  }

  func makeLookupPipeline (_ channel: Channel) -> EventLoopFuture<Void> {
    return channel.pipeline.addHandler(BackPressureHandler()).flatMap { [weak self] in
      let handlers = [
        self!.makeLookupMessagesHandler(),
        self!.errorChannelHandler
      ]
      return channel.pipeline.addHandlers(handlers)
    }
  }
}

fileprivate final class Cache {

  private var cache = [String: ChannelHandler]()

  func get<T> (_ type: T.Type) -> OrClause {
    let key = String(describing: type)
    let result = cache[key]
    let cacheIt: (ChannelHandler) -> Void = { [weak self] (value) in
      guard let self = self else {
        return
      }
      self.cache[key] = value
    }
    return OrClause(cacheIt: cacheIt, result: result)
  }

  struct OrClause {

    private let cacheIt: (ChannelHandler) -> Void
    private let result: ChannelHandler?

    init (cacheIt: @escaping (ChannelHandler) -> Void, result: ChannelHandler?) {
      self.cacheIt = cacheIt
      self.result = result
    }

    func orBuildAndCache (builder build: () -> ChannelHandler) -> ChannelHandler {
      if let cached = result {
        return cached
      }
      let builded = build()
      cacheIt(builded)
      return builded
    }
  }
}
