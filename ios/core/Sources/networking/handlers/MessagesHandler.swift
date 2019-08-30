
import NIO
import Foundation

import logging
import utils


final class MessagesHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = Inbound
  typealias OutboundOut = Outbound

  let messagesProcessor = Atomic<InboundHandler?>(nil)
  let eventsProcessor = Atomic<EventHandler?>(nil)

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let outbound = unwrapInboundIn(data)

    guard let processor = messagesProcessor.load() else {
      log.warn("there is no messages processor for {} client", context.remoteAddress!)
      return
    }
    if let response = processor(outbound) {
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
      return
    }
  }

  func send (_ message: Outbound) {

  }
}
