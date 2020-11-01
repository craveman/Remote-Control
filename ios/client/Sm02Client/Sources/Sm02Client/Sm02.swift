
import struct Foundation.UUID
import class NIOConcurrencyHelpers.ConditionLock
import class NIO.EventLoopFuture

public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void


public class Sm02 {

  typealias Container = Singletons & NetworkServiceFactory

  public static var isConnected: Bool {
    return client.isConnected
  }

  static var container: Container = DependencyContainer()

  private static var client: Sm02Client = Sm02DummyClient()

  public static func connect (to remote: RemoteAddress) -> Result<AuthenticationStatus, Error> {
    client.close()
    if client is Sm02DummyClient {
      client = container.makeTcpClient()
    }

    let error = client.connect(to: remote)
    if let error = error {
      return .failure(error)
    }

    return authenticate(with: remote)
  }

  private static func authenticate (with remote: RemoteAddress) -> Result<AuthenticationStatus, Error> {
    let lock = ConditionLock(value: 0)
    var responseStatus: AuthenticationStatus? = nil

    let uuid = on(message: { inbound in
      guard case let Inbound.authentication(status) = inbound else {
        return
      }
      lock.lock()
      responseStatus = status
      lock.unlock(withValue: 1)
    })
    defer {
      remove(messageHandler: uuid)
    }

    let request = Outbound.authenticate(
      device: .remoteControl,
      code: remote.code,
      name: "com.inspirationApp.RemoteControl",
      version: 1
    )
    send(message: request)

    if (lock.lock(whenValue: 1, timeoutSeconds: 5) == false) {
      print("Sm02 - WARN: timeout condition for the lock is unsuccessful")
    }
    lock.unlock()

    guard let result = responseStatus else {
      let error = ConnectionError.responseTimeout(5)
      return .failure(error)
    }
    return .success(result)
  }

  public static func send (message: Outbound) {
    client.send(message: message)
  }

  @discardableResult
  public static func on (message handler: @escaping InboundHandler) -> UUID {
    return container.messagesManager.add(handler: handler)
  }

  @discardableResult
  public static func on (event handler: @escaping EventHandler) -> UUID {
    return container.eventsManager.add(handler: handler)
  }

  @discardableResult
  public static func remove (messageHandler uuid: UUID) -> Bool {
    return container.messagesManager.remove(handler: uuid)
  }

  @discardableResult
  public static func remove (eventHandler uuid: UUID) -> Bool {
    return container.eventsManager.remove(handler: uuid)
  }

  public static func disconnect () {
    send(message: Outbound.disconnect)
    client.close()
  }
}
