
import NIO
import NIOExtras

import networking
import logging


final class InboundToByteBufferEncoder: ChannelOutboundHandler, Loggable {

  private typealias Encoder = (Inbound) -> [UInt8]
  // private static let encoders: [Inbound: Encoder] = [
  //     .tock: encodeTock
  // ]

  private static func encodeTock (response: Inbound) -> [UInt8] {
    return [0xF2, 0x01]
  }

  public typealias OutboundIn = Inbound
  public typealias OutboundOut = ByteBuffer

  public func write (context: ChannelHandlerContext, data: NIOAny, promise: EventLoopPromise<Void>?) {
    let inbound = unwrapOutboundIn(data)

    var buffer = context.channel.allocator.buffer(capacity: 2)

    switch inbound {
    case let .genericResponse(request):
      buffer.writeInteger(inbound.tag as UInt8)
      buffer.writeInteger(1 as UInt8)
      buffer.writeInteger(request as UInt8)
    default:
      log.error("Unsupported type {}", inbound)
      return
    }

    let out = wrapOutboundOut(buffer)
    context.write(out, promise: nil)
  }
}
