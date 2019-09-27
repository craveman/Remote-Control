
import NIO
import Sm02Client


final class MessagesHandler: ChannelInboundHandler {

  typealias InboundIn = Outbound
  typealias OutboundOut = Inbound

  let messagesProcessor: MessagesProcessor
  let eventsProcessor: EventsProcessor

  init (_ messagesProcessor: @escaping MessagesProcessor,
        _ eventsProcessor: @escaping EventsProcessor
  ) {
    self.messagesProcessor = messagesProcessor
    self.eventsProcessor = eventsProcessor
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let outbound = unwrapInboundIn(data)

    let result: Inbound
    if let response = messagesProcessor(outbound) {
      result = response
    } else if case .genericResponse(_, _) = outbound {
      return
    } else {
      result = .genericResponse(request: outbound.tag)
    }

    let out = wrapOutboundOut(result)
    context.writeAndFlush(out, promise: nil)
  }
}
