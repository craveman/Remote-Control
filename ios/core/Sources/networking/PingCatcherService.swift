
import NIO
import Dispatch

import logging


typealias PingCatchedAction = (_ remoteAddress: SocketAddress) -> Void

final class PingCatcherService: Loggable {

  let port: Int
  let group: MultiThreadedEventLoopGroup
  let bootstrap: DatagramBootstrap

  var channel: Channel?

  init (listen port: Int = 21075, _ action: @escaping PingCatchedAction) {
    self.port = port
    let handler = Catcher(action)

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = DatagramBootstrap(group: group)
        .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 10)
        .channelInitializer { channel in
            channel.pipeline.addHandler(handler)
    }
  }

  deinit {
    stop()
    try! group.syncShutdownGracefully()
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
}

private class Catcher: ChannelInboundHandler, Loggable {

  private static let ping = [UInt8] ("PING".utf8)

  typealias InboundIn = AddressedEnvelope<ByteBuffer>

  let handler: PingCatchedAction

  init (_ handler: @escaping PingCatchedAction) {
    self.handler = handler
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let requestEnvelope = unwrapInboundIn(data)
    var requestData = requestEnvelope.data
    let bytes = requestData.readBytes(length: requestData.readableBytes)!

    context.close(promise: nil)
    if bytes == Catcher.ping {
      let remoteAddress = requestEnvelope.remoteAddress
      handler(remoteAddress)
    } else {
      log.error("wrong ping message - {}", bytes)
    }
  }

  public func channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush()
  }

  public func errorCaught(ctx: ChannelHandlerContext, error: Error) {
    log.error("connection error occured - {}", error)
    ctx.close(promise: nil)
  }
}

