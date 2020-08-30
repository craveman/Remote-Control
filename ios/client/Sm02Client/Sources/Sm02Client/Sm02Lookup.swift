
import struct Foundation.UUID

public typealias ServerFoundHandler = (_ server: RemoteAddress) -> Void


public class Sm02Lookup {

  typealias Container = Singletons & NetworkServiceFactory

  static var container: Container = Sm02.container

  private static var server: Sm02LookupServer = Sm02DummyLookupServer()

  public static func start (listen port: Int = 21075) {
    server.stop()
    if server is Sm02DummyLookupServer {
      server = container.makeUdpLookup()
    }
    server.start(listen: port)
  }

  @discardableResult
  public static func on (server handler: @escaping ServerFoundHandler) -> UUID {
    return container.serversManager.add(handler: handler)
  }

  @discardableResult
  public static func remove (serverFoundHandler uuid: UUID) -> Bool {
    return container.serversManager.remove(handler: uuid)
  }

  public static func stop () {
    server.stop()
  }
}
