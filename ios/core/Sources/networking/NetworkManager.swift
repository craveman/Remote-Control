
import Foundation


public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void

public protocol NetworkManagerProtocol {

  func on (messages handler: @escaping InboundHandler) -> UUID

  func on (events handler: @escaping EventHandler) -> UUID

  func remove (messagesHandler: UUID)

  func remove (eventsHandler: UUID)

  func start ()

  func stop ()

  func send (message: Outbound)
}

public class NetworkManager: NetworkManagerProtocol {

  public static let shared: NetworkManagerProtocol = NetworkManager()

  public func on (messages handler: @escaping InboundHandler) -> UUID {
    let uuid = UUID()
    return uuid
  }

  public func on (events handler: @escaping EventHandler) -> UUID {
    let uuid = UUID()
    return uuid
  }

  public func remove (messagesHandler: UUID) {

  }

  public func remove (eventsHandler: UUID) {

  }

  public func start () {

  }

  public func stop () {

  }

  public func send (message: Outbound) {

  }
}