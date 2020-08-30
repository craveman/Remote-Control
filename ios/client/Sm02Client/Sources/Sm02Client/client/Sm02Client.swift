import class NIO.EventLoopFuture

protocol Sm02Client {

  var isConnected: Bool { get }

  func connect (to remote: RemoteAddress) -> Error?
  
  @discardableResult
  func send (message: Outbound) -> EventLoopFuture<Void>?

  func close ()
}
