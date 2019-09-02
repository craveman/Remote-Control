
import NIO

import logging


final class LogOnErrorHandler: ChannelInboundHandler, Loggable {

  typealias Factory = Singletons
  typealias InboundIn = NIOAny

  let events: EventsManager

  init (factory: Factory) {
    events = factory.eventsManager
  }

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    log.error("eduring processing request from '{}', this error occured - {}", context.remoteAddress!, error)
    context.fireErrorCaught(error)
  }
}
