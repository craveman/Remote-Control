
public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void

public protocol NetworkManagerProtocol {

  func on (messages handler: @escaping InboundHandler)

  func on (events handler: @escaping EventHandler)

  func start ()

  func stop ()

  func send (message: Outbound)
}

public class NetworkManager: NetworkManagerProtocol {

  public static let shared: NetworkManagerProtocol = NetworkManager()

  public func on (messages handler: @escaping InboundHandler) {

  }

  public func on (events handler: @escaping EventHandler) {

  }

  public func start () {

  }

  public func stop () {

  }

  public func send (message: Outbound) {

  }
}