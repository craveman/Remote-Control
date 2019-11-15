
class Sm02DummyClient: Sm02Client {

  var isConnected = false

  func connect (to remote: RemoteServer) {
    // noop
  }

  func send (message: Outbound) {
    // noop
  }

  func close () {
    // noop
  }
}
