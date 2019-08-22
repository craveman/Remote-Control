
import NIO
import NIOExtras

import networking
import logging


final class TcpServer: Loggable {

  let host: String
  let port: Int
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ServerBootstrap
  let messagesProcessor: ThreadSafeProperty<MessagesProcessor>
  let eventsProcessor: ThreadSafeProperty<EventsProcessor>

  var channel: Channel?

  init (host: String = "127.0.0.1", port: Int = 21074) {
    self.host = host
    self.port = port

    messagesProcessor = ThreadSafeProperty<MessagesProcessor>()
    eventsProcessor = ThreadSafeProperty<EventsProcessor>()

    let sharedDecoderHandler = ByteBufferToOutboundDecoder()
    let sharedLogOnErrorHandler = LogOnErrorHandler()
    let sharedCloseOnErrorHandler = NIOCloseOnErrorHandler()
    let sharedEncoderHandler = InboundToByteBufferEncoder()

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ServerBootstrap(group: group)
        .serverChannelOption(ChannelOptions.backlog, value: 64)
        .serverChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        // .childChannelInitializer { channel in
        //     channel.pipeline.addHandler(BackPressureHandler()).flatMap { () in
        //         channel.pipeline.addHandlers([
        //             ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
        //             sharedDecoderHandler,
        //             MessagesHandler(messagesProcessor, eventsProcessor),
        //             logOnErrorHandler,
        //             closeOnErrorHandler,
        //         ])
        //     }
        // }
        .childChannelOption(ChannelOptions.socket(IPPROTO_TCP, TCP_NODELAY), value: 1)
        .childChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .childChannelOption(ChannelOptions.maxMessagesPerRead, value: 1)
        .childChannelOption(ChannelOptions.recvAllocator, value: AdaptiveRecvByteBufferAllocator())

    _ = bootstrap.childChannelInitializer { channel in
        channel.pipeline.addHandler(BackPressureHandler()).flatMap { [unowned self] () in
            channel.pipeline.addHandlers([
                ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
                LengthFieldPrepender(lengthFieldLength: .two),
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
