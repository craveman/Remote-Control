
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
    print("Sm02UdpLookupServer - INFO: creating a bootstrap...")
    
    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = DatagramBootstrap(group: group)
      .channelOption(ChannelOptions.socketOption(.so_reuseaddr), value: 1)
      .channelInitializer({ channel in
        container.makeLookupPipeline(channel)
      })
    
    print("Sm02UdpLookupServer - INFO: the bootstrap was created")
  }
  
  deinit {
    stop()

    print("Sm02UdpLookupServer - INFO: terminating NIO group")

    do {
      try group.syncShutdownGracefully()
    } catch {
      print("Sm02UdpLookupServer - ERROR: closing error - \(error)")
    }
  }
  
  func start (listen port: Int) {
    print("Sm02UdpLookupServer - INFO: starting the server on port \(port)...")
    do {
      channel = try bootstrap.bind(host: "0.0.0.0", port: port).wait()
      print("Sm02UdpLookupServer - INFO: the server was started")
    } catch {
      print("Sm02UdpLookupServer - ERROR: starting error - \(error)")
    }
  }
  
  func stop () {
    print("Sm02UdpLookupServer - INFO: stopping the server...")
    if let channel = channel {
      let _ = channel.close();
      self.channel = nil
    }
    print("Sm02UdpLookupServer - INFO: the server was stopped")
  }
}
