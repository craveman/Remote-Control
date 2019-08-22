
public protocol NetworkManager {

  func on (messages handler: @escaping (_ message: Inbound) -> Void)

  func on (events handler: @escaping (_ event: ConnectionEvent) -> Void)

  func start ()

  func stop ()

  func send (message: Outbound)
}
