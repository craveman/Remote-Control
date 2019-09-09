
import NIO

import logging


final class TcpClient: Loggable {

  typealias Factory = Singletons & ChannelHandlerFactory

  let host: String
  let port: Int
  let factory: Factory
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap

  var channel: Channel?

  init (for host: String, port: Int = 21074, factory: Factory) {
    self.host = host
    self.port = port
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

    log.debug("connecting to {}:{}", host, port)
    do {
      channel = try bootstrap.connect(host: host, port: port).wait()
      log.debug("connected")
    } catch {
      log.error("connection error - {}", error)
    }
  }

  func stop () {
    log.debug("closing..")
    if let channel = channel {
      let _ = channel.close()
      self.channel = nil
    }
    log.debug("closed")
  }

  func close () {
    stop()
    do {
      try group.syncShutdownGracefully()
    } catch {
      log.error("closing error - {}", error)
    }
  }

  func send (_ message: Outbound) {
    channel?.writeAndFlush(message, promise: nil)
  }
}
