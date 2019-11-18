
protocol Sm02Client {

  var isConnected: Bool { get }

  func connect (to remote: RemoteAddress) -> Result<Void, Error>

  func send (message: Outbound)

  func close ()
}
