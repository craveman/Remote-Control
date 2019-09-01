
import NIO
import Foundation

import logging
import utils


final class AuthenticationHandler: ChannelInboundHandler, RemovableChannelHandler, Loggable {

  typealias InboundIn = Inbound
  typealias OutboundOut = Outbound

  public func channelActive (context: ChannelHandlerContext) {
    log.debug("connected to {}", context.remoteAddress!)
    // send auth
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let request = unwrapInboundIn(data)

    context.pipeline.removeHandler(self)
  }
}
