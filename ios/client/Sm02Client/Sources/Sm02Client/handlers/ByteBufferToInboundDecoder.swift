
import NIO
import NIOExtras


final class ByteBufferToInboundDecoder: ChannelInboundHandler {

  private typealias Decoder = (inout ByteBuffer) -> Inbound?
  private static let decoders: [UInt8: Decoder] = [
    0x0B: decodeBroadcast,
    0x1A: decodeDeviceList,
    0x21: decodeEthernetDisplay,
    0x22: decodeFightResult,
    0x11: decodePassiveMax,
    0x12: decodePauseFinished,
    0x1B: decodeVideoReady,
    0x1C: decodeVideoReceived,
    0x24: decodeAuthentication,
    0xAA: decodeGenericResponse,
  ]

  private static func decodeBroadcast (buffer: inout ByteBuffer) -> Inbound? {
    guard let weapon = buffer.readWeapon() else {
      print("ERROR: The 'broadcast' message doesn't have 'weapon' field")
      return nil
    }
    guard let leftFlag = buffer.readFlagState() else {
      print("ERROR: The 'broadcast' message doesn't have 'leftFlag' field")
      return nil
    }
    guard let rightFlag = buffer.readFlagState() else {
      print("ERROR: The 'broadcast' message doesn't have 'rightFlag' field")
      return nil
    }
    guard let timer = buffer.readUInt32() else {
      print("ERROR: The 'broadcast' message doesn't have 'timer' field")
      return nil
    }
    guard let timerState = buffer.readTimerState() else {
      print("ERROR: The 'broadcast' message doesn't have 'timerState' field")
      return nil
    }
    return .broadcast(weapon: weapon, left: leftFlag, right: rightFlag, timer: timer, timerState: timerState)
  }

  private static func decodeDeviceList (buffer: inout ByteBuffer) -> Inbound? {
    guard let devicesCount = buffer.readUInt8() else {
      print("ERROR: The 'decodeDeviceList' message doesn't have 'count' field")
      return nil
    }

    var devices: [Device] = []
    for index in 0..<devicesCount {
      guard let device = buffer.readDevice() else {
        print("ERROR: The 'decodeDeviceList' message doesn't have 'device[{}]' field", index)
        return nil
      }
      devices.append(device)
    }
    return .deviceList(devices: devices)
  }

  private static func decodeEthernetDisplay (buffer: inout ByteBuffer) -> Inbound? {
    guard let period = buffer.readUInt8() else {
      print("ERROR: The 'ethernetDisplay' message doesn't have 'period' field")
      return nil
    }
    guard let time = buffer.readUInt32() else {
      print("ERROR: The 'ethernetDisplay' message doesn't have 'time' field")
      return nil
    }
    guard let leftSide = buffer.readSide() else {
      print("ERROR: The 'ethernetDisplay' message doesn't have 'left side' field")
      return nil
    }
    guard let rightSide = buffer.readSide() else {
      print("ERROR: The 'ethernetDisplay' message doesn't have 'right side' field")
      return nil
    }
    return .ethernetDisplay(period: period, time: time, left: leftSide, right: rightSide)
  }

  private static func decodeFightResult (buffer: inout ByteBuffer) -> Inbound? {
    guard let decision = buffer.readDecision() else {
      print("ERROR: The 'fightResult' message doesn't have 'result' field")
      return nil
    }
    return .fightResult(result: decision)
  }

  private static func decodePassiveMax (buffer: inout ByteBuffer) -> Inbound? {
    return .passiveMax
  }

  private static func decodePauseFinished (buffer: inout ByteBuffer) -> Inbound? {
    return .pauseFinished
  }

  private static func decodeVideoReady (buffer: inout ByteBuffer) -> Inbound? {
    guard let name = buffer.readString() else {
      print("ERROR: The 'videoReady' message doesn't have 'name' field")
      return nil
    }
    return .videoReady(name: name)
  }

  private static func decodeVideoReceived (buffer: inout ByteBuffer) -> Inbound? {
    return .videoReceived
  }

  private static func decodeAuthentication (buffer: inout ByteBuffer) -> Inbound? {
    guard let status = buffer.readAuthenticationStatus() else {
      print("ERROR: The 'authentication' message doesn't have 'status' field")
      return nil
    }
    return .authentication(status: status)
  }

  private static func decodeGenericResponse (buffer: inout ByteBuffer) -> Inbound? {
    guard let request = buffer.readUInt8() else {
      print("ERROR: The 'genericResponse' message doesn't have 'request' field")
      return nil
    }
    return .genericResponse(request: request)
  }

  typealias InboundIn = ByteBuffer
  typealias InboundOut = Inbound

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    switch decode(data) {
    case .success(let outbound):
      context.fireChannelRead(wrapInboundOut(outbound))
    case .failure(let error):
      context.fireErrorCaught(error)
    }
  }

  func decode (_ data: NIOAny) -> Result<Inbound, ConnectionError> {
    var buffer = unwrapInboundIn(data)
    guard let tag = buffer.readUInt8() else {
      return .failure(.parsingdError("The message doesn't have a tag"))
    }
    guard let decoder = ByteBufferToInboundDecoder.decoders[tag] else {
      return .failure(.parsingdError("The message doen't have a decoder for the tag - '\(tag)'"))
    }
    guard let _ = buffer.readUInt8() else {
      return .failure(.parsingdError("The message '\(tag)' doesn't have status"))
    }
    guard let outbound = decoder(&buffer) else {
      return .failure(.parsingdError("The message '\(tag)' doesn't have a decoder"))
    }
    return .success(outbound)
  }

  public func channelReadComplete (context: ChannelHandlerContext) {
    context.flush()
  }

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    print("ERROR: during channel handling - \(error)")
    context.close(promise: nil)
  }
}

extension ByteBuffer {

  func getUInt8 (at index: Int) -> UInt8? {
    return getBytes(at: index, length: 1).map { $0[0] }
  }

  mutating func readUInt8 () -> UInt8? {
    return readBytes(length: 1).map {
        return $0[0]
    }
  }
}

extension ByteBuffer {

  mutating func readBytes () -> [UInt8]? {
    return readUInt8()
        .map(Int.init)
        .flatMap {
            return self.readBytes(length: $0)
        }
  }
}

extension ByteBuffer {

  mutating func readUInt32 () -> UInt32? {
    return readBytes(length: 4).map { array in
      let one = UInt32(array[0])
      let two = UInt32(array[1])
      let three = UInt32(array[2])
      let four = UInt32(array[3])

      return (four << 24) + (three << 16) + (two << 8) + one
    }
  }
}

extension ByteBuffer {

  mutating func readString () -> String? {
    return readUInt8()
        .map(Int.init)
        .flatMap {
            return self.readString(length: $0)
        }
  }
}

extension ByteBuffer {

  mutating func readWeapon () -> Weapon? {
    return readUInt8().flatMap {
        return Weapon(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readFlagState () -> FlagState? {
    return readUInt8().flatMap {
        return FlagState(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readTimerState () -> TimerState? {
    return readUInt8().flatMap {
        return TimerState(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readDeviceType () -> DeviceType? {
    return readUInt8().flatMap {
        return DeviceType(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readStatusCard () -> StatusCard? {
    return readUInt8().flatMap {
        return StatusCard(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readDecision () -> Decision? {
    return readUInt8().flatMap {
        return Decision(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readDevice () -> Device? {
    guard let name = readString() else {
      return nil
    }
    guard let type = readDeviceType() else {
      return nil
    }
    return Device(name: name, type: type)
  }
}

extension ByteBuffer {

  mutating func readSide () -> Side? {
    guard let score = readUInt8() else {
      return nil
    }
    guard let card = readStatusCard() else {
      return nil
    }
    guard let name = readString() else {
      return nil
    }
    return Side(score: score, card: card, name: name)
  }
}

extension ByteBuffer {

  mutating func readAuthenticationStatus () -> AuthenticationStatus? {
    return readUInt8().flatMap {
        return AuthenticationStatus(rawValue: $0)
    }
  }
}

