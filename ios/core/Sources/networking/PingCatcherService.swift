
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
    log.debug("starting...")
    channel = try! bootstrap.bind(host: "127.0.0.1", port: port).wait()
    log.debug("started")
  }

  func stop () {
    log.debug("stopping...")
    if channel != nil, channel!.isActive {
      let _ = channel!.close()
      channel = nil
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

    if bytes == PingCatcherHandler.ping {
      let serverAddress = requestEnvelope.remoteAddress
      let event = ConnectionEvent.pingCatched(serverAddress: serverAddress)
      events.fire(it: event)
      context.close(promise: nil)
    } else {
      log.error("wrong ping message - {}", bytes)
    }
  }
}

