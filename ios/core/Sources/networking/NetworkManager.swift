
import Foundation


public typealias InboundHandler = (_ message: Inbound) -> Outbound?
public typealias EventHandler = (_ event: ConnectionEvent) -> Void

public protocol NetworkManagerProtocol {

  @discardableResult
  func on (messages handler: @escaping InboundHandler) -> NetworkManagerProtocol

  @discardableResult
  func on (events handler: @escaping EventHandler) -> NetworkManagerProtocol

  @discardableResult
  func start () -> NetworkManagerProtocol

  func stop ()

  func send (message: Outbound)
}

public class NetworkManager: NetworkManagerProtocol {

  public static let shared: NetworkManagerProtocol = NetworkManager()

  let pingCatcher: PingCatcherService
  let client: TcpClient

  init () {
    client = TcpClient()
    pingCatcher = PingCatcherService { [weak client] (remoteAddress) in
      client?.connect(to: remoteAddress)
    }
  }

  deinit {
    stop()
  }

  public func on (messages handler: @escaping InboundHandler) -> NetworkManagerProtocol {
    client.on(messages: handler)
    return self
  }

  public func on (events handler: @escaping EventHandler) -> NetworkManagerProtocol {
    EventService.shared.add(handler: handler)
    return self
  }

  public func start () -> NetworkManagerProtocol {
    pingCatcher.start()
    return self
  }

  public func stop () {
    pingCatcher.stop()
    client.close()
  }

  public func send (message: Outbound) {
    client.send(message)
  }
}
