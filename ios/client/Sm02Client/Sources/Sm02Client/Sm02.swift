

public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void


public class Sm02 {
  
  public static var connected: Bool {
    return client?.connected ?? false
  }
  
  private static let context = ConnectionContext()
  private static var client: Sm02Client? = Sm02DummyClient()

  public static func connect (to remote: RemoteServer) {
    if let client = client {
      client.close()
    } else {
      client = Sm02TcpClient(context: Sm02.context)
    }
    client?.connect(to: remote)
  }

  public static func send (message: Outbound) {
    client?.send(message: message)
  }

  public static func on (message handler: @escaping InboundHandler) {
    context.add(handler)
  }

  public static func on (event handler: @escaping EventHandler) {
    context.add(handler)
  }
  
  public static func disconnect () {
    client?.close()
  }
}

class ConnectionContext {
  
  private var messageHandlers = ThreadedArray<InboundHandler>()
  private var eventHandlers = ThreadedArray<EventHandler>()
  
  func add (_ handler: @escaping InboundHandler) {
    messageHandlers.append(handler)
  }
  
  func getAllMessageHandlers () -> [InboundHandler] {
    return messageHandlers.unthreaded
  }
  
  func add (_ handler: @escaping EventHandler) {
    eventHandlers.append(handler)
  }
  
  func getAllEventHandlers () -> [EventHandler] {
    return eventHandlers.unthreaded
  }
}
