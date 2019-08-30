
import NIO
import NIOExtras

import logging


final class OutboundToByteBufferEncoder: ChannelOutboundHandler, Loggable {

  private typealias Encoder = (Outbound) -> [UInt8]
  // private static let encoders: [Outbound: Encoder] = [
  //     .tock: encodeTock
  // ]

  private static func encodeTick (response: Outbound) -> [UInt8] {
    return [0xF1, 0x00]
  }

  typealias OutboundIn = Outbound
  typealias OutboundOut = ByteBuffer

  public func write (context: ChannelHandlerContext, data: NIOAny, promise: EventLoopPromise<Void>?) {
    let outbound = unwrapOutboundIn(data)

    // guard let encoder = encoders[outbound] else {
    //   context.fireErrorCaught(.encodingInboundFail("=("))
    //   return
    // }
    let encoder = OutboundToByteBufferEncoder.encodeTick
    let bytes = encoder(outbound)

    var buffer = context.channel.allocator.buffer(capacity: bytes.count)
    buffer.writeBytes(bytes)

    let out = wrapOutboundOut(buffer)
    context.write(out, promise: nil)
  }
}
