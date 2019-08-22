
import NIO

import logging
import networking

final class MessagesHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = Outbound
  typealias OutboundOut = Inbound

  let messagesProcessor: ThreadSafeProperty<MessagesProcessor>
  let eventsProcessor: ThreadSafeProperty<EventsProcessor>

  init (_ messagesProcessor: ThreadSafeProperty<MessagesProcessor>,
        _ eventsProcessor: ThreadSafeProperty<EventsProcessor>
  ) {
    self.messagesProcessor = messagesProcessor
    self.eventsProcessor = eventsProcessor
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let outbound = unwrapInboundIn(data)
    guard let processor = messagesProcessor.value else {
      log.warn("there is no messages processor for {} client", context.remoteAddress!)
      return
    }
    if let response = processor(outbound) {
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
      return
    }
  }
}
