
protocol Sm02Client {
  
  var connected: Bool { get }
  
  func connect (to remote: RemoteServer)
  
  func send (message: Outbound)
  
  func close ()
}
