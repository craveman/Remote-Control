
import networking


public typealias MessagesProcessor = (_ message: Outbound) -> Inbound?
public typealias EventsProcessor = (_ event: ConnectionEvent) -> Void

public class SM02 {

  let pingBroadcastService: PingBroadcastService
  let tcpServer: TcpServer

  public init (config: SM02Config = SM02Config()) {
    pingBroadcastService = PingBroadcastService(
        host: config.udpBroadcast.host,
        port: config.udpBroadcast.port
    )!
    tcpServer = TcpServer(
        host: config.tcpServer.host,
        port: config.tcpServer.port
    )
  }

  deinit {
    stop()
  }

  public func start () -> SM02 {
    tcpServer.start()
    pingBroadcastService.start()
    return self
  }

  public func stop () {
    pingBroadcastService.stop()
    tcpServer.stop()
  }

  public func on (messages handler: @escaping MessagesProcessor) -> SM02 {
    tcpServer.messagesProcessor.store(handler)
    return self
  }

  public func on (events handler: @escaping EventsProcessor) -> SM02 {
    tcpServer.eventsProcessor.store(handler)
    return self
  }
}

