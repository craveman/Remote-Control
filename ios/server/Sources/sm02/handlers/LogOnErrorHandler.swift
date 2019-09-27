
import NIO


final class LogOnErrorHandler: ChannelInboundHandler {

  typealias InboundIn = NIOAny

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    let remoteAddress = context.remoteAddress
          .map { String(describing: $0) }
          ?? "<none>"

    print("ERROR: eduring processing request from '\(remoteAddress)', this error occured - \(error)")
    context.fireErrorCaught(error)
  }
}
