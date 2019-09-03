
import NIO
import NIOExtras

import logging


protocol Singletons {

  var eventsManager: EventsManager { get }
  var messagesManager: MessagesManager { get }
}

protocol NetworkServiceFactory {

  func makePingCatcherService () -> PingCatcherService

  func makeTcpClient (for host: String, port: Int) -> TcpClient
}

protocol ChannelHandlerFactory {

  var decoderChanneHandler: ChannelHandler { get }
  var encoderChanneHandler: ChannelHandler { get }
  var errorChannelHandler: ChannelHandler { get }

  func makeMainChannelHandler () -> ChannelHandler

  func makeClientPipeline (_ channel: Channel) -> EventLoopFuture<Void>
}

class DependencyContainer: Singletons, Loggable {

  lazy private(set) var eventsManager = EventsManager()
  lazy private(set) var messagesManager = MessagesManager()
}

public struct RuntimeError: Error {

  public let message: String

  public init(_ message: String) {
    self.message = message
  }
}

extension DependencyContainer: NetworkServiceFactory {

  func makePingCatcherService () -> PingCatcherService {
    return PingCatcherService(factory: self)
  }

  func makeTcpClient (for host: String, port: Int) -> TcpClient {
    return TcpClient(for: host, port: port, factory: self)
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
        LogOnErrorHandler(factory: self!)
      }
  }

  func makeMainChannelHandler () -> ChannelHandler {
    return MessagesHandler(factory: self)
  }

  func makeClientPipeline (_ channel: Channel) -> EventLoopFuture<Void> {
    return channel.pipeline.addHandler(BackPressureHandler()).flatMap { [weak self] in
      let handlers = [
        ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
        LengthFieldPrepender(lengthFieldLength: .two),
        IdleStateHandler(readTimeout: .seconds(6)),
        TickTockHandler(factory: self!),
        self!.decoderChanneHandler,
        self!.encoderChanneHandler,
        self!.makeMainChannelHandler(),
        self!.errorChannelHandler,
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
