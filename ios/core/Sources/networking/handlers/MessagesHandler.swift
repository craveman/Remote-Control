
import NIO
import Foundation

import logging
import utils


final class MessagesHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = Inbound
  typealias OutboundOut = Outbound

  let messagesProcessor = Atomic<InboundHandler?>(nil)
  let eventsProcessor = Atomic<EventHandler?>(nil)

  var connectionContext: ChannelHandlerContext?

  public func channelActive (context: ChannelHandlerContext) {
    log.debug("connected to {}", context.remoteAddress!)
    connectionContext = context
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let request = unwrapInboundIn(data)

    guard let processor = messagesProcessor.load() else {
      log.warn("there is no messages processor for {} client", context.remoteAddress!)
      return
    }

    let response = processor(request) ?? Outbound.genericResponse(request: request.tag)

    let out = wrapOutboundOut(response)
    context.writeAndFlush(out, promise: nil)
  }

  func send (_ message: Outbound) {
    let out = wrapOutboundOut(message)
    connectionContext?.writeAndFlush(out, promise: nil)
  }
}
