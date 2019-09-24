
protocol Sm02Client {
  
  var isConnected: Bool { get }
  
  func connect (to remote: RemoteServer)
  
  func send (message: Outbound)
  
  func close ()
}
