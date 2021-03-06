
import NIO
import class NIOConcurrencyHelpers.NIOAtomic
import class NIO.EventLoopFuture

class Sm02TcpClient: Sm02Client {

  typealias Container = Singletons & ChannelHandlerFactory

  let container: Container
  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap
  let events: HandlersManager<ConnectionEvent>

  var channel: Channel?
  var isConnected: Bool {
    return channel?.isWritable ?? false
  }
  private let isNormalDisconnect = NIOAtomic<Bool>.makeAtomic(value: false)

  init (container: Container) {
    self.container = container
    events = container.eventsManager

    group = MultiThreadedEventLoopGroup(numberOfThreads: 4)
    bootstrap = ClientBootstrap(group: group)
      .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
      .connectTimeout(.seconds(1))
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

    isNormalDisconnect.store(false)
    let _ = channel?.closeFuture.always { [weak self] (result) in
      self?.events.fire(it: .disconnected)
      if self?.isNormalDisconnect.load() == false {
        self?.events.fire(it: .serverDown)
      }
    }

    events.fire(it: .connected)
    return nil
  }

  func send (message: Outbound) {
    if isConnected == false {
      return
    }
    channel?.writeAndFlush(message, promise: nil)
  }

  func close () {
    isNormalDisconnect.store(true)
    if let channel = channel {
      let _ = channel.close()
      self.channel = nil
    }
  }
}
