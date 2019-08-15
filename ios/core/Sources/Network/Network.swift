
public protocol Network {

  func on (message handler: @escaping (_ message: Inbound) -> Void)

  func on (event handler: @escaping (_ event: ConnectionEvent) -> Void)

  func start ()

  func stop ()

  func send (message: Outbound)
}
