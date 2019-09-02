
import NIO

import logging


final class TcpClient: Loggable {

  typealias Factory = Singletons & ChannelHandlerFactory

  let serverAddress: SocketAddress
  let factory: Factory
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap

  var channel: Channel?

  init (for serverAddress: SocketAddress, factory: Factory) {
    self.serverAddress = serverAddress
    self.factory = factory

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ClientBootstrap(group: group)
      .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
      .channelInitializer({ channel in
        factory.makeClientPipeline(channel)
      })
  }

  deinit {
    close()
  }

  func start () {
    if let _ = channel {
      close()
    }
    log.debug("connecting...")
    channel = try! bootstrap.connect(to: serverAddress).wait()
    log.debug("connected")
  }

  func stop () {
    log.debug("closing..")
    if channel != nil, channel!.isActive {
      let _ = channel!.close()
      channel = nil
    }
    log.debug("closed")
  }

  func close () {
    stop()
    try! group.syncShutdownGracefully()
  }

  func send (_ message: Outbound) {
    channel?.writeAndFlush(message, promise: nil)
  }
}
