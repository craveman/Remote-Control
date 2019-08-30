
import NIO
import NIOExtras

import logging


final class TcpClient: Loggable {

  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap
  let messagesHandler: MessagesHandler

  var channel: Channel?

  init () {
    messagesHandler = MessagesHandler()

    let sharedDecoderHandler = ByteBufferToInboundDecoder()
    let sharedLogOnErrorHandler = LogOnErrorHandler()
    let sharedCloseOnErrorHandler = NIOCloseOnErrorHandler()
    let sharedEncoderHandler = OutboundToByteBufferEncoder()

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ClientBootstrap(group: group)
        .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .channelInitializer { [unowned messagesHandler] (channel) in
            channel.pipeline.addHandler(BackPressureHandler()).flatMap {
              channel.pipeline.addHandlers([
                  ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
                  LengthFieldPrepender(lengthFieldLength: .two),
                  sharedDecoderHandler,
                  sharedEncoderHandler,
                  messagesHandler,
                  sharedLogOnErrorHandler,
                  sharedCloseOnErrorHandler,
              ])
          }
        }
  }

  deinit {
    close()
    try! group.syncShutdownGracefully()
  }

  func connect (to remoteAddress: SocketAddress) {
    if let _ = channel {
      close()
    }
    log.debug("connecting...")
    channel = try! bootstrap.connect(to: remoteAddress).wait()
    log.debug("connected")
  }

  func close () {
    log.debug("closing..")
    let _ = channel?.close()
    log.debug("closed")
  }

  func send (_ message: Outbound) {
    messagesHandler.send(message)
  }

  func on (messages handler: @escaping InboundHandler) {
    messagesHandler.messagesProcessor.store(handler)
  }

  func on (events handler: @escaping EventHandler) {
    messagesHandler.eventsProcessor.store(handler)
  }
}
