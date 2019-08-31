
import NIO
import NIOExtras

import networking
import logging
import utils


final class TcpServer: Loggable {

  let host: String
  let port: Int
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ServerBootstrap
  let messagesProcessor: Atomic<MessagesProcessor?>
  let eventsProcessor: Atomic<EventsProcessor?>

  var channel: Channel?

  init (host: String = "127.0.0.1", port: Int = 21074) {
    self.host = host
    self.port = port

    messagesProcessor = Atomic<MessagesProcessor?>(nil)
    eventsProcessor = Atomic<EventsProcessor?>(nil)

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
              MessagesHandler(self.messagesProcessor, self.eventsProcessor),
              sharedLogOnErrorHandler,
              sharedCloseOnErrorHandler,
          ])
        }
    }
  }

  deinit {
    stop()
    try! group.syncShutdownGracefully()
  }

  func start () {
    log.debug("starting...")
    channel = try! bootstrap.bind(host: host, port: port).wait()
    log.debug("started")
  }

  func stop () {
    log.debug("stopping..")
    let _ = channel?.close()
    log.debug("stopped")
  }
}
