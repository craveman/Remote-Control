
import NIO
import NIOExtras
import Sm02Client


typealias MessagesProcessor = (_ message: Outbound) -> Inbound?
typealias EventsProcessor = (_ event: ConnectionEvent) -> Void


final class Sm02Server {

  let code: [UInt8]
  let messagesProcessor: MessagesProcessor
  let eventsProcessor: EventsProcessor
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ServerBootstrap

  var channel: Channel?

  init (code: [UInt8],
        onMessage: @escaping MessagesProcessor,
        onEvent: @escaping EventsProcessor
  ) {
    self.code = code
    messagesProcessor = onMessage
    eventsProcessor = onEvent

    let sharedTickTockHandler = TickTockHandler()
    let sharedDecoderHandler = ByteBufferToOutboundDecoder()
    let sharedEncoderHandler = InboundToByteBufferEncoder()
    let sharedLogOnErrorHandler = LogOnErrorHandler()
    let sharedCloseOnErrorHandler = NIOCloseOnErrorHandler()

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ServerBootstrap(group: group)
        .serverChannelOption(ChannelOptions.backlog, value: 64)
        .serverChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .childChannelOption(ChannelOptions.socket(IPPROTO_TCP, TCP_NODELAY), value: 1)
        .childChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .childChannelOption(ChannelOptions.maxMessagesPerRead, value: 1)
        .childChannelOption(ChannelOptions.recvAllocator, value: AdaptiveRecvByteBufferAllocator())

    _ = bootstrap.childChannelInitializer { channel in
        channel.pipeline.addHandler(BackPressureHandler()).flatMap { [unowned self] () in
          channel.pipeline.addHandlers([
              ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
              LengthFieldPrepender(lengthFieldLength: .two),
              sharedTickTockHandler,
              sharedDecoderHandler,
              sharedEncoderHandler,
              AuthorizationHandler(self.code),
              MessagesHandler(self.messagesProcessor, self.eventsProcessor),
              sharedLogOnErrorHandler,
              sharedCloseOnErrorHandler,
          ])
        }
    }
  }

  deinit {
    stop()
  }

  func start () {
    print("starting server...")
    channel = try! bootstrap.bind(host: "0.0.0.0", port: 21074).wait()
    print("server started")
  }

  func stop () {
    print("stopping server..")
    if let channel = channel {
      let _ = channel.close()
      self.channel = nil
    }
    try! group.syncShutdownGracefully()
    print("server stopped")
  }
}
