import class NIO.EventLoopFuture

class Sm02DummyClient: Sm02Client {

  var isConnected = false

  func connect (to remote: RemoteAddress) -> Error? {
    return nil
  }

  func send (message: Outbound) {
    // noop
  }

  func close () {
    // noop
  }
}
