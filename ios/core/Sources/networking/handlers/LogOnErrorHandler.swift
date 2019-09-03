
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
    let remoteAddress = context.remoteAddress
          .map { String(describing: $0) }
          ?? "<none>"

    log.error("error during processing request from '{}', this error occured - {}", remoteAddress, error)
    context.fireErrorCaught(error)
  }
}
