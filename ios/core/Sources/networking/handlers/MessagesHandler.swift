
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

    let (response, handled) = messages.fire(it: request)
    if handled == false {
      log.warn("there is no messages processor for {} client", context.remoteAddress!)
      return
    }

    if let response = response {
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
      return
    }

    if request.hasGenericResponse() {
      let response = Outbound.genericResponse(request: request.tag)
      let out = wrapOutboundOut(response)
      context.writeAndFlush(out, promise: nil)
      return
    }

    let error = ConnectionError.mandatoryResponseAbsent(request: request)
    context.fireErrorCaught(error)
  }
}

extension Inbound {

  func hasGenericResponse () -> Bool {
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
