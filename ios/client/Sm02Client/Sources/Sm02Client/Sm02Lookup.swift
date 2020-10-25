
import struct Foundation.UUID

public typealias ServerFoundHandler = (_ server: RemoteAddress) -> Void


public class Sm02Lookup {

  typealias Container = Singletons & NetworkServiceFactory

  static var container: Container = Sm02.container
  
  public static var DEFAULT_SM_UDP_PORT = 21075

  private static var server: Sm02LookupServer = Sm02DummyLookupServer()

  public static func start (listen port: Int) {
    print("Sm02Lookup - INFO: starting the lookup server...")
    server.stop()
    if server is Sm02DummyLookupServer {
      server = container.makeUdpLookup()
    }
    server.start(listen: port)
    print("Sm02Lookup - INFO: the lookup server was started")
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
    print("Sm02Lookup - INFO: stopping the lookup server...")
    server.stop()
    print("Sm02Lookup - INFO: the lookup server was stopped")
  }
}
