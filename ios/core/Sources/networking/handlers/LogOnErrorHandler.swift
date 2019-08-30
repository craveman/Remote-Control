
import NIO

import logging


final class LogOnErrorHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = NIOAny

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    log.error("eduring processing request from '{}', this error occured - {}", context.remoteAddress!, error)
    context.fireErrorCaught(error)
  }
}
