
import NIO
import NIOExtras


final class OutboundToByteBufferEncoder: ChannelOutboundHandler {

  typealias OutboundIn = Outbound
  typealias OutboundOut = ByteBuffer

  public func write (context: ChannelHandlerContext, data: NIOAny, promise: EventLoopPromise<Void>?) {
    if data.description.contains("ByteBuffer") {
      context.write(data, promise: nil)
      return
    }

    let outbound = unwrapOutboundIn(data)

    var buffer = context.channel.allocator.buffer(capacity: 2)
    buffer.writeInteger(outbound.tag as UInt8)
    buffer.writeInteger(0 as UInt8)

    write(outbound, to: &buffer)

    let out = wrapOutboundOut(buffer)
    context.write(out, promise: nil)
  }

  private func write (_ outbound: Outbound, to buffer: inout ByteBuffer) {
    switch outbound {
    case let .setName(person, name):
      buffer.writeInteger(person.rawValue as UInt8)
      buffer.ext_write(name)
    case let .setScore(person, score):
      buffer.writeInteger(person.rawValue as UInt8)
      buffer.writeInteger(score as UInt8)
    case let .setCard(person, status):
      buffer.writeInteger(person.rawValue as UInt8)
      buffer.writeInteger(status.rawValue as UInt8)
    case let .setPriority(person):
      buffer.writeInteger(person.rawValue as UInt8)
    case let .setPeriod(period):
      buffer.writeInteger(period as UInt8)
    case let .setWeapon(weapon):
      buffer.writeInteger(weapon.rawValue as UInt8)
    case let .setTimer(time, mode):
      buffer.writeInteger(time as UInt32)
      buffer.writeInteger(mode.rawValue as UInt8)
    case let .startTimer(state):
      buffer.writeInteger(state.rawValue as UInt8)
    case let .visibility(video, photo, passive, country):
      buffer.writeInteger((video ? 1 : 0) as UInt8)
      buffer.writeInteger((photo ? 1 : 0) as UInt8)
      buffer.writeInteger((passive ? 1 : 0) as UInt8)
      buffer.writeInteger((country ? 1 : 0) as UInt8)
    case let .videoCounters(left, right):
      buffer.writeInteger(left as UInt8)
      buffer.writeInteger(right as UInt8)
    case let .passiveTimer(shown, locked, defaultMilliseconds):
      buffer.writeInteger((shown ? 1 : 0) as UInt8)
      buffer.writeInteger((locked ? 1 : 0) as UInt8)
      buffer.writeInteger(defaultMilliseconds as UInt32)
    case let .setDefaultTime(time):
      buffer.writeInteger(time as UInt32)
    case let .setCompetition(name):
      buffer.ext_write(name)
    case let .videoRoutes(cameras):
      buffer.writeInteger(UInt8(cameras.count))
      cameras.forEach {
        buffer.ext_write($0)
      }
    case let .loadFile(name):
      buffer.ext_write(name)
    case let .player(speed, recordMode, timestamp):
      buffer.writeInteger(speed as UInt8)
      buffer.writeInteger(recordMode.rawValue as UInt8)
      buffer.writeInteger(timestamp as UInt32)
    case let .record(recordMode):
      buffer.writeInteger(recordMode.rawValue as UInt8)
    case let .ethernetNextOrPrevious(next):
      buffer.writeInteger((next ? 1 : 0) as UInt8)
    case let .authenticate(device, code, name, version):
      buffer.writeInteger(device.rawValue as UInt8)
      buffer.ext_write(code)
      buffer.ext_write(name)
      buffer.writeInteger(version as UInt8)
    case let .genericResponse(_, request):
      buffer.writeInteger(request as UInt8)
    default:
      return
    }
  }
}

extension ByteBuffer {

  mutating func ext_write (_ value: [UInt8]) {
    writeInteger(UInt8(value.count))
    writeBytes(value)
  }
}

extension ByteBuffer {

  mutating func ext_write (_ value: String) {
    let valueBytes: [UInt8] = Array(value.utf8)
    ext_write(valueBytes)
  }
}

extension ByteBuffer {

  mutating func ext_write (_ value: Camera) {
    ext_write(value.name)
    ext_write(value.target)
  }
}
