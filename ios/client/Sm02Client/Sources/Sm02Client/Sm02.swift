

public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void


public class Sm02 {
  
  typealias Container = Singletons & NetworkServiceFactory
  
  public static var isConnected: Bool {
    return client.isConnected
  }
  
  static var container: Container = DependencyContainer()
  
  private static var client: Sm02Client = Sm02DummyClient()

  public static func connect (to remote: RemoteServer) {
    client.close()
    if client is Sm02DummyClient {
      client = container.makeTcpClient()
    }
    client.connect(to: remote)
  }

  public static func send (message: Outbound) {
    client.send(message: message)
  }

  public static func on (message handler: @escaping InboundHandler) {
    container.messagesManager.add(handler: handler)
  }

  public static func on (event handler: @escaping EventHandler) {
    container.eventsManager.add(handler: handler)
  }
  
  public static func disconnect () {
    client.close()
  }
}
