
import NIO
import Sm02Client


final class MessagesHandler: ChannelInboundHandler {

  typealias InboundIn = Outbound
  typealias OutboundOut = Inbound

  let messagesProcessor: Atomic<MessagesProcessor?>
  let eventsProcessor: Atomic<EventsProcessor?>

  init (_ messagesProcessor: Atomic<MessagesProcessor?>,
        _ eventsProcessor: Atomic<EventsProcessor?>
  ) {
    self.messagesProcessor = messagesProcessor
    self.eventsProcessor = eventsProcessor
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let outbound = unwrapInboundIn(data)
    guard let processor = messagesProcessor.load() else {
      let remoteAddress = context.remoteAddress
          .map { String(describing: $0) }
          ?? "<none>"

      print("WARN: there is no messages processor for \(remoteAddress) client")
      return
    }
    if let response = processor(outbound) {
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
      return
    }
  }
}
