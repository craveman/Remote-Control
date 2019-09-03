
import Foundation

import logging
import utils


public typealias InboundHandler = (_ message: Inbound) -> Void
public typealias EventHandler = (_ event: ConnectionEvent) -> Void

public protocol NetworkManagerProtocol {

  var state: NetworkState { get }

  @discardableResult
  func handle (messages handler: @escaping InboundHandler) -> NetworkManagerProtocol

  @discardableResult
  func on (events handler: @escaping EventHandler) -> NetworkManagerProtocol

  @discardableResult
  func start () -> NetworkManagerProtocol

  func stop ()

  func close ()

  func send (message: Outbound)
}

public enum NetworkState {

  case idle
  case pingAwaiting
  case running
  case off
}

public class NetworkManager: NetworkManagerProtocol, Loggable {

  typealias Factory = Singletons & NetworkServiceFactory

  public static let shared = NetworkManager()

  public var state: NetworkState {
    return networkState.load()
  }

  private let factory: Factory
  private let events: EventsManager
  private let messages: MessagesManager

  private var networkState: Atomic<NetworkState>
  private var pingCatcher: PingCatcherService?
  private var tcpClient: TcpClient?

  init (factory: Factory = DependencyContainer()) {
    self.factory = factory
    events = factory.eventsManager
    messages = factory.messagesManager

    networkState = Atomic(.idle)
    pingCatcher = factory.makePingCatcherService()
    tcpClient = nil

    events.add(handler: { [weak self] (event) in
      guard case let .pingCatched(serverAddress) = event else {
        return
      }
      guard let self = self else {
        return
      }
      self.tcpClient = self.factory.makeTcpClient(for: serverAddress)
      self.tcpClient!.start()

      self.pingCatcher?.close()
      self.pingCatcher = nil
    })
  }

  deinit {
    close()
  }

  public func handle (messages handler: @escaping InboundHandler) -> NetworkManagerProtocol {
    messages.set(handler: handler)
    return self
  }

  public func on (events handler: @escaping EventHandler) -> NetworkManagerProtocol {
    events.add(handler: handler)
    return self
  }

  public func start () -> NetworkManagerProtocol {
    changeState(expected: [.idle], { [weak self] (state) in
      guard let self = self else {
        return state
      }
      if self.tcpClient != nil {
        self.tcpClient!.start()
        return .running
      }
      if self.pingCatcher == nil {
        self.pingCatcher = self.factory.makePingCatcherService()
      }
      self.pingCatcher!.start()
      return .pingAwaiting
    })
    return self
  }

  public func stop () {
    changeState(expected: [.pingAwaiting, .running], { [weak self] (state) in
      guard let self = self else {
        return state
      }
      self.tcpClient?.stop()
      self.pingCatcher?.stop()
      return .idle
    })
  }

  public func close () {
    changeState(expected: [.idle, .pingAwaiting, .running], { [weak self] (state) in
      guard let self = self else {
        return state
      }
      self.messages.clear()
      self.events.clear()
      self.pingCatcher?.close()
      self.pingCatcher = nil
      self.tcpClient?.close()
      self.tcpClient = nil
      return .off
    })
  }

  public func send (message: Outbound) {
    tcpClient?.send(message)
  }

  private func changeState (expected: [NetworkState], _ action: @escaping (NetworkState) -> NetworkState) {
    let _ = networkState.mutatingSync({ [weak self] currentState -> NetworkState in
      if expected.contains(currentState) {
        currentState = action(currentState)
      } else {
        self!.log.warn("expected the following states {}, but current is {}", expected, currentState)
      }
      return currentState
    })
  }
}
