
import NIO

import logging


final class LogOnErrorHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = NIOAny

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    let remoteAddress = context.remoteAddress
          .map { String(describing: $0) }
          ?? "<none>"

    log.error("eduring processing request from '{}', this error occured - {}", remoteAddress, error)
    context.fireErrorCaught(error)
  }
}
