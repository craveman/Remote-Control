
import NIO
import Foundation

import logging
import utils


final class MessagesHandler: ChannelInboundHandler, Loggable {

  typealias Factory = Singletons & ChannelHandlerFactory
  typealias InboundIn = Inbound
  typealias OutboundOut = Outbound

  let messages: MessagesManager

  init (factory: Factory) {
    messages = factory.messagesManager
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let request = unwrapInboundIn(data)

    if messages.fire(it: request) == false {
      log.warn("there is no messages processor for {} client", context.remoteAddress!)
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
         .ethernetDisplay,
         .fightResult,
         .videoReady,
         .videoReceived:
      return true
    default:
      return false
    }
  }
}
