
import NIO
import NIOExtras
import Sm02Client


final class InboundToByteBufferEncoder: ChannelOutboundHandler {

  public typealias OutboundIn = Inbound
  public typealias OutboundOut = ByteBuffer

  public func write (context: ChannelHandlerContext, data: NIOAny, promise: EventLoopPromise<Void>?) {
    let inbound = unwrapOutboundIn(data)

    var buffer = context.channel.allocator.buffer(capacity: 2)
    buffer.writeInteger(inbound.tag as UInt8)

    switch inbound {
    case let .broadcast(weapon, left, right, timer, timerState):
      buffer.writeInteger(0 as UInt8)
      buffer.writeInteger(weapon.rawValue as UInt8)
      buffer.writeInteger(left.rawValue as UInt8)
      buffer.writeInteger(right.rawValue as UInt8)
      buffer.writeInteger(timer as UInt32)
      buffer.writeInteger(timerState.rawValue as UInt8)
    case let .deviceList(devices):
      buffer.writeInteger(1 as UInt8)
      buffer.writeInteger(UInt8(devices.count))
      for device in devices {
        buffer.writeInteger(device.type.rawValue as UInt8)
        buffer.ext_writeString(device.name)
      }
    case let .ethernetDisplay(period, time, left, right):
      buffer.writeInteger(0 as UInt8)
      buffer.writeInteger(left.score as UInt8)
      buffer.writeInteger(right.score as UInt8)
      buffer.writeInteger(period as UInt8)
      buffer.writeInteger(left.card.rawValue as UInt8)
      buffer.writeInteger(right.card.rawValue as UInt8)
      buffer.ext_writeString(left.name)
      buffer.ext_writeString(right.name)
      buffer.writeInteger(time as UInt32)
    case let .fightResult(result):
      buffer.writeInteger(0 as UInt8)
      buffer.writeInteger(result.rawValue as UInt8)
    case .passiveMax:
      buffer.writeInteger(1 as UInt8)
    case .pauseFinished:
      buffer.writeInteger(1 as UInt8)
    case let .videoReady(name):
      buffer.writeInteger(0 as UInt8)
      buffer.ext_writeString(name)
    case .videoReceived:
      buffer.writeInteger(0 as UInt8)
    case let .authentication(status):
      buffer.writeInteger(status.rawValue as UInt8)
    case let .genericResponse(request):
      buffer.writeInteger(1 as UInt8)
      buffer.writeInteger(request as UInt8)
    }

    let out = wrapOutboundOut(buffer)
    context.write(out, promise: nil)
  }
}

extension ByteBuffer {

  mutating func ext_writeString (_ string: String) {
    let bytes = Array(string.utf8)
    writeInteger(bytes.count)
    writeBytes(bytes)
  }
}
