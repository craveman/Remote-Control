
import NIO
import NIOExtras

import logging

enum EncodingError: Error {

  case bad
  case noEncoderFor(Outbound)
}

final class OutboundToByteBufferEncoder: ChannelOutboundHandler, Loggable {

  private static func write (to bytes: inout [UInt8], _ string: String) {
    let stringBytes: [UInt8] = Array(string.utf8)
    bytes.append(UInt8(stringBytes.count))
    bytes.append(contentsOf: stringBytes)
  }

  private typealias Encoder = (Outbound) -> Result<[UInt8], EncodingError>
  private static let encoders: [UInt8: Encoder] = [
      0xF1: { response in .success([response.tag, 0x00]) },
      0x01: encodeSetName
  ]

  private static func encodeSetName (response: Outbound) -> Result<[UInt8], EncodingError> {
    guard case let .setName(person, name) = response else {
      return .failure(.bad)
    }
    var result: [UInt8] = [response.tag, 0x00]
    result.append(person.rawValue)
    write(to: &result, name)
    return .success(result)
  }

  typealias OutboundIn = Outbound
  typealias OutboundOut = ByteBuffer

  public func write (context: ChannelHandlerContext, data: NIOAny, promise: EventLoopPromise<Void>?) {
    let outbound = unwrapOutboundIn(data)

    guard let encoder = OutboundToByteBufferEncoder.encoders[outbound.tag] else {
      let error = EncodingError.noEncoderFor(outbound)
      context.fireErrorCaught(error)
      return
    }

    switch encoder(outbound) {
    case let .failure(error):
      context.fireErrorCaught(error)
      return
    case let .success(bytes):
      var buffer = context.channel.allocator.buffer(capacity: bytes.count)
      buffer.writeBytes(bytes)

      let out = wrapOutboundOut(buffer)
      context.write(out, promise: nil)
    }
  }
}
