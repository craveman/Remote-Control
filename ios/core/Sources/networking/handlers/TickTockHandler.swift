
import NIO
import Foundation

import logging


final class TickTockHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = ByteBuffer
  typealias OutboundOut = ByteBuffer

  static let TICK_TAG: UInt8 = 0xF1
  static let TOCK_TAG: UInt8 = 0xF2
  static let TOCK_RESPONSE_STATUS: UInt8 = 0x01

  public func channelActive (context: ChannelHandlerContext) {
    log.debug("connected to {}", context.remoteAddress!)
    connectionContext = context
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let buffer = unwrapInboundIn(data)

    let tag = buffer.getInteger(at: 0, as: UInt8.self)
    if tag != TOCK_TAG {
      context.fireChannelRead(data)
      return
    }

    let status = buffer.getInteger(at: 0, as: UInt8.self)
    if status != TOCK_RESPONSE_STATUS {
      let error =
      context.fireErrorCaught(error)
      return
    }


  }
}
