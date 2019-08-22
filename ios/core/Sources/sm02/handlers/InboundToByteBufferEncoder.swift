
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

    // guard let encoder = encoders[inbound] else {
    //   context.fireErrorCaught(.encodingInboundFail("=("))
    //   return
    // }
    let encoder = InboundToByteBufferEncoder.encodeTock
    let bytes = encoder(inbound)

    var buffer = context.channel.allocator.buffer(capacity: bytes.count)
    buffer.writeBytes(bytes)

    let out = wrapOutboundOut(buffer)
    context.write(out, promise: nil)
  }
}
