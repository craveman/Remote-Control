
protocol Sm02LookupServer {

  var isStarted: Bool { get }

  func start (listen port: Int)

  func stop ()
}
