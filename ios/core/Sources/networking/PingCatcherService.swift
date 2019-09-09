
import NIO
import Dispatch

import logging


final class PingCatcherService: Loggable {

  typealias Factory = Singletons

  let port: Int
  let group: MultiThreadedEventLoopGroup
  let bootstrap: DatagramBootstrap

  var channel: Channel?

  init (listen port: Int = 21075, factory: Factory) {
    self.port = port

    let handler = PingCatcherHandler(factory: factory)

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = DatagramBootstrap(group: group)
        .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 10)
        .channelInitializer { channel in
            channel.pipeline.addHandler(handler)
    }
  }

  deinit {
    close()
  }

  func start () {
    log.debug("starting on port {}", port)
    do {
      channel = try bootstrap.bind(host: "127.0.0.1", port: port).wait()
      log.debug("started")
    } catch {
      log.error("connection error - {}", error)
    }
  }

  func stop () {
    log.debug("stopping...")
    if let channel = channel {
      let _ = channel.close()
      self.channel = nil
    }
    log.debug("stopped")
  }

  func close () {
    stop()
    try! group.syncShutdownGracefully()
  }
}

fileprivate final class PingCatcherHandler: ChannelInboundHandler, Loggable {

  private static let ping = [UInt8] ("PING".utf8)

  typealias Factory = Singletons
  typealias InboundIn = AddressedEnvelope<ByteBuffer>

  let events: EventsManager

  init (factory: Factory) {
    events = factory.eventsManager
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let requestEnvelope = unwrapInboundIn(data)
    var requestData = requestEnvelope.data
    let bytes = requestData.readBytes(length: requestData.readableBytes)!

    if bytes != PingCatcherHandler.ping {
      log.error("wrong ping message - {}", bytes)
    }

    var host: String?
    switch requestEnvelope.remoteAddress {
    case let .v4(address):
      host = address.host
    case let .v6(address):
      host = address.host
    default:
      log.error("unsupported remote server address type {}", requestEnvelope.remoteAddress)
      return
    }

    if let host = host {
      let event = ConnectionEvent.pingCatched(remoteHost: host)
      events.fire(it: event)
      context.close(promise: nil)
    }
  }
}

