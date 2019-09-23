
class Sm02TcpClient: Sm02Client {
  
  let context: ConnectionContext
  var connected = true
  
  init (context: ConnectionContext) {
    self.context = context
  }
  
  deinit {
    close()
  }
  
  func connect (to remote: RemoteServer) {
    
  }
  
  func send (message: Outbound) {
    
  }
  
  func close () {
    connected = false
  }
}
