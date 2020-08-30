
import NIO

class Sm02UdpLookupServer: Sm02LookupServer {

  typealias Container = Singletons & ChannelHandlerFactory

  let container: Container
  let group: MultiThreadedEventLoopGroup
  let bootstrap: DatagramBootstrap

  var channel: Channel?
  var isStarted: Bool {
    return channel?.isWritable ?? false
  }

  init (container: Container) {
    self.container = container

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = DatagramBootstrap(group: group)
      .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
      .channelInitializer({ channel in
        container.makeLookupPipeline(channel)
      })
  }

  deinit {
    stop()

    print("terminating NIO group")
    do {
      try group.syncShutdownGracefully()
    } catch {
      print("ERROR: closing error - \(error)")
    }
  }

  func start (listen port: Int) {
    do {
      channel = try bootstrap.bind(host: "0.0.0.0", port: port).wait()
    } catch {
      print("ERROR: starting error - \(error)")
    }
  }

  func stop () {
    if let channel = channel {
      let _ = channel.close();
      self.channel = nil
    }
  }
}
