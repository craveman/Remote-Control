
import NIO
import Foundation

import networking
import logging
import utils


final class TickTockHandler: ChannelInboundHandler, Loggable {

  typealias InboundIn = ByteBuffer
  typealias OutboundOut = ByteBuffer
  typealias OutboundIn = ByteBuffer

  private static let TICK_TAG: UInt8 = 0xF1
  private static let TICK_REQUEST_STATUS: UInt8 = 0x00
  private static let TOCK_TAG: UInt8 = 0xF2
  private static let TOCK_RESPONSE_STATUS: UInt8 = 0x01

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    var buffer = unwrapInboundIn(data)

    guard let tag = buffer.getInteger(at: 0, as: UInt8.self) else {
      let message = "message doesn't contain a tag"
      let error = ConnectionError.parsingdError(message)
      context.fireErrorCaught(error)
      return
    }

    if tag != TickTockHandler.TICK_TAG {
      context.fireChannelRead(data)
      return
    }
    buffer.moveReaderIndex(forwardBy: 1)

    guard let status = buffer.readInteger(as: UInt8.self) else {
      let message = "a tick message doesn't contain a status"
      let error = ConnectionError.parsingdError(message)
      context.fireErrorCaught(error)
      return
    }

    if status != TickTockHandler.TICK_REQUEST_STATUS {
      let message = "tick (tag: \(tag)) message has invalid status value - \(status)"
      let error = ConnectionError.parsingdError(message)
      context.fireErrorCaught(error)
      return
    }

    var tockMessage = context.channel.allocator.buffer(capacity: 2)
    tockMessage.writeInteger(TickTockHandler.TOCK_TAG as UInt8)
    tockMessage.writeInteger(TickTockHandler.TOCK_RESPONSE_STATUS as UInt8)

    let out = self.wrapOutboundOut(tockMessage)
    context.writeAndFlush(out, promise: nil)
  }
}