
import networking
import logging


public typealias MessagesProcessor = (_ message: Outbound) -> Inbound?
public typealias EventsProcessor = (_ event: ConnectionEvent) -> Void

public class SM02: Loggable {

  let pingBroadcastService: PingBroadcastService
  let tcpServer: TcpServer

  public init (config: SM02Config = SM02Config()) {
    pingBroadcastService = PingBroadcastService(
        host: config.udpBroadcast.host,
        port: config.udpBroadcast.port
    )
    tcpServer = TcpServer(
        host: config.tcpServer.host,
        port: config.tcpServer.port
    )
  }

  deinit {
    close()
  }

  public func handle (messages handler: @escaping MessagesProcessor) -> SM02 {
    tcpServer.messagesProcessor.store(handler)
    return self
  }

  public func on (events handler: @escaping EventsProcessor) -> SM02 {
    tcpServer.eventsProcessor.store(handler)
    return self
  }

  public func start () -> SM02 {
    log.debug("starting...")
    tcpServer.start()
    pingBroadcastService.start()
    log.debug("start")
    return self
  }

  public func stop () {
    log.debug("stopping..")
    pingBroadcastService.stop()
    tcpServer.stop()
    log.debug("stopped")
  }

  public func close () {
    log.debug("closing...")
    pingBroadcastService.stop()
    tcpServer.close()
    log.debug("closed")
  }
}

