import class NIO.EventLoopFuture

protocol Sm02Client {

  var isConnected: Bool { get }

  func connect (to remote: RemoteAddress) -> Error?
  
  func send (message: Outbound)

  func close ()
}
