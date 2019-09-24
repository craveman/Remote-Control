
import NIO


final class LogOnErrorHandler: ChannelInboundHandler {

  typealias Container = Singletons
  typealias InboundIn = NIOAny

  let events: HandlersManager<ConnectionEvent>

  init (container: Container) {
    events = container.eventsManager
  }

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    let remoteAddress = context.remoteAddress
          .map { String(describing: $0) }
          ?? "<none>"

    print("ERROR: during processing request from '{}', this error occured - {}", remoteAddress, error)
    context.fireErrorCaught(error)
  }
}
