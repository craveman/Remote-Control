
import NIO


class Sm02TcpClient: Sm02Client {

  typealias Container = Singletons & ChannelHandlerFactory

  let container: Container
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap

  var channel: Channel?
  var isConnected: Bool {
    return channel?.isWritable ?? false
  }

  init (container: Container) {
    self.container = container

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ClientBootstrap(group: group)
      .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
      .connectTimeout(.seconds(2))
      .channelInitializer({ channel in
        container.makeClientPipeline(channel)
      })
  }

  deinit {
    close()

    print("terminating NIO group")
    do {
      try group.syncShutdownGracefully()
    } catch {
      print("ERROR: closing error - \(error)")
    }
  }

  func connect (to remote: RemoteAddress) -> Error? {
    do {
      channel = try bootstrap.connect(host: remote.ip, port: 21074).wait()
    } catch ChannelError.connectTimeout(let timeAmount) {
      let timeout = (Int) (timeAmount.nanoseconds / 1_000_000_000)
      return ConnectionError.connectionTimeout(timeout)
    } catch is NIOConnectionError {
      return ConnectionError.connectionRefused
    } catch {
      return error
    }
    return nil
  }

  func send (message: Outbound) {
    channel?.writeAndFlush(message, promise: nil)
  }

  func close () {
    if let channel = channel {
      let _ = channel.close()
      self.channel = nil
    }
  }
}
