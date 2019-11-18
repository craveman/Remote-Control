
class Sm02DummyClient: Sm02Client {

  var isConnected = false

  func connect (to remote: RemoteAddress) -> Result<Void, Error> {
    return .success(())
  }

  func send (message: Outbound) {
    // noop
  }

  func close () {
    // noop
  }
}
