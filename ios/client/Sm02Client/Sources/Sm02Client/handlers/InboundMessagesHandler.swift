
import NIO
import Foundation


final class InboundMessagesHandler: ChannelInboundHandler {

  typealias Container = Singletons & ChannelHandlerFactory
  typealias InboundIn = Inbound
  typealias OutboundOut = Outbound

  let messages: HandlersManager<Inbound>

  init (container: Container) {
    messages = container.messagesManager
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let request = unwrapInboundIn(data)

    if messages.fire(it: request) == false {
      print("WARN: there is no messages processor for {} client", context.remoteAddress!)
      return
    }

    if request.hasGenericResponse {
      let response = Outbound.genericResponse(request: request.tag)
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
    }
  }
}

extension Inbound {

  var hasGenericResponse: Bool {
    switch self {
    case .broadcast,
         .quit,
         .additionalState,
         .competition,
         .fightResult,
         .videoReady,
         .videoReceived:
      return true
    default:
      return false
    }
  }
}
