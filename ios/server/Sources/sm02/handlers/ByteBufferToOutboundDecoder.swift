
import NIO
import NIOExtras

import Sm02Client


final class ByteBufferToOutboundDecoder: ChannelInboundHandler {

  private typealias Decoder = (inout ByteBuffer) -> Outbound?
  private static let decoders: [UInt8: Decoder] = [
    0x01: decodeSetName,
    0x03: decodeSetScore,
    0x04: decodeSetCard,
    0x05: decodeSetPriority,
    0x06: decodeSetPeriod,
    0x07: decodeSetWeapon,
    0x08: decodeSetTimer,
    0x09: decodeStartTimer,
    0x0A: decodeSwap,
    0x0C: decodeVisibility,
    0x0D: decodeVideoCounters,
    0x0F: decodeDisconnect,
    0x10: decodePassiveTimer,
    0x13: decodeSetDefaultTime,
    0x14: decodeSetCompetition,
    0x15: decodeVideoRoutes,
    0x16: decodeLoadFile,
    0x17: decodePlayer,
    0x18: decodeRecord,
    0x19: decodeDevicesRequest,
    0x1D: decodeReset,
    0x1E: decodeEthernetNextOrPrevious,
    0x1F: decodeEthernetApply,
    0x20: decodeEthernetFinishAsk,
    0x23: decodeAuthenticate,
    0xAA: decodeGenericResponse,
  ]

  private static func decodeSetName (buffer: inout ByteBuffer) -> Outbound? {
    guard let personType = buffer.readPersonType() else {
      print("ERROR: The 'setName' message doesn't have 'person type' field")
      return nil
    }
    guard let name = buffer.readString() else {
      print("ERROR: The 'setName' message doesn't have 'name' field")
      return nil
    }
    return .setName(person: personType, name: name)
  }

  private static func decodeSetScore (buffer: inout ByteBuffer) -> Outbound? {
    guard let personType = buffer.readPersonType() else {
      print("ERROR: The 'setScore' message doesn't have 'person type' field")
      return nil
    }
    guard let score = buffer.readUInt8() else {
      print("ERROR: The 'setScore' message doesn't have 'score' field")
      return nil
    }
    return .setScore(person: personType, score: score)
  }

  private static func decodeSetCard (buffer: inout ByteBuffer) -> Outbound? {
    guard let personType = buffer.readPersonType() else {
      print("ERROR: The 'setCard' message doesn't have 'person type' field")
      return nil
    }
    guard let statusCard = buffer.readStatusCard() else {
      print("ERROR: The 'setCard' message doesn't have 'status card' field")
      return nil
    }
    return .setCard(person: personType, status: statusCard)
  }

  private static func decodeSetPriority (buffer: inout ByteBuffer) -> Outbound? {
    guard let personType = buffer.readPersonType() else {
      print("ERROR: The 'setPriority' message doesn't have 'person type' field")
      return nil
    }
    return .setPriority(person: personType)
  }

  private static func decodeSetPeriod (buffer: inout ByteBuffer) -> Outbound? {
    guard let period = buffer.readUInt8() else {
      print("ERROR: The 'setPeriod' message doesn't have 'period' field")
      return nil
    }
    return .setPeriod(period: period)
  }

  private static func decodeSetWeapon (buffer: inout ByteBuffer) -> Outbound? {
    guard let weapon = buffer.readWeapon() else {
      print("ERROR: The 'setWeapon' message doesn't have 'weapon' field")
      return nil
    }
    return .setWeapon(weapon: weapon)
  }

  private static func decodeSetTimer (buffer: inout ByteBuffer) -> Outbound? {
    guard let time = buffer.readUInt32() else {
      print("ERROR: The 'setTimer' message doesn't have 'time' field")
      return nil
    }
    guard let mode = buffer.readTimerMode() else {
      print("ERROR: The 'setTimer' message doesn't have 'mode' field")
      return nil
    }
    return .setTimer(time: time, mode: mode)
  }

  private static func decodeStartTimer (buffer: inout ByteBuffer) -> Outbound? {
    guard let state = buffer.readTimerState() else {
      print("ERROR: The 'startTimer' message doesn't have 'state' field")
      return nil
    }
    return .startTimer(state: state)
  }

  private static func decodeSwap (buffer: inout ByteBuffer) -> Outbound? {
    return .swap
  }

  private static func decodeVisibility (buffer: inout ByteBuffer) -> Outbound? {
    guard let video = buffer.readUInt8() else {
      print("ERROR: The 'visibility' message doesn't have 'video' field")
      return nil
    }
    guard let photo = buffer.readUInt8() else {
      print("ERROR: The 'visibility' message doesn't have 'photo' field")
      return nil
    }
    guard let passive = buffer.readUInt8() else {
      print("ERROR: The 'visibility' message doesn't have 'passive' field")
      return nil
    }
    guard let country = buffer.readUInt8() else {
      print("ERROR: The 'visibility' message doesn't have 'country' field")
      return nil
    }
    return .visibility(
        video: video == 0,
        photo: photo == 0,
        passive: passive == 0,
        country: country == 0
    )
  }

  private static func decodeVideoCounters (buffer: inout ByteBuffer) -> Outbound? {
    guard let left = buffer.readUInt8() else {
      print("ERROR: The 'videoCounters' message doesn't have 'left' field")
      return nil
    }
    guard let right = buffer.readUInt8() else {
      print("ERROR: The 'videoCounters' message doesn't have 'right' field")
      return nil
    }
    return .videoCounters(left: left, right: right)
  }

  private static func decodeDisconnect (buffer: inout ByteBuffer) -> Outbound? {
    return .disconnect
  }

  private static func decodePassiveTimer (buffer: inout ByteBuffer) -> Outbound? {
    guard let show = buffer.readUInt8() else {
      print("ERROR: The 'passiveTimer' message doesn't have 'show' field")
      return nil
    }
    guard let locked = buffer.readUInt8() else {
      print("ERROR: The 'passiveTimer' message doesn't have 'locked' field")
      return nil
    }
    guard let timer = buffer.readUInt32() else {
      print("ERROR: The 'passiveTimer' message doesn't have 'timer' field")
      return nil
    }
    return .passiveTimer(shown: show == 0, locked: locked == 0, defaultMilliseconds: timer)
  }

  private static func decodeSetDefaultTime (buffer: inout ByteBuffer) -> Outbound? {
    guard let time = buffer.readUInt32() else {
      print("ERROR: The 'setDefaultTime' message doesn't have 'time' field")
      return nil
    }
    return .setDefaultTime(time: time)
  }

  private static func decodeSetCompetition (buffer: inout ByteBuffer) -> Outbound? {
    guard let name = buffer.readString() else {
      print("ERROR: The 'setCompetition' message doesn't have 'name' field")
      return nil
    }
    return .setCompetition(name: name)
  }

  private static func decodeVideoRoutes (buffer: inout ByteBuffer) -> Outbound? {
    guard let camerasCount = buffer.readUInt8() else {
      print("ERROR: The 'videoRoutes' message doesn't have 'cameras count' field")
      return nil
    }

    var cameras: [Camera] = []
    for index in 0..<camerasCount {
      guard let camera = buffer.readCamera() else {
        print("ERROR: The 'videoRoutes' message doesn't have 'camera[{}]' field", index)
        return nil
      }
      cameras.append(camera)
    }
    return .videoRoutes(cameras: cameras)
  }

  private static func decodeLoadFile (buffer: inout ByteBuffer) -> Outbound? {
    guard let name = buffer.readString() else {
      print("ERROR: The 'loadFile' message doesn't have 'name' field")
      return nil
    }
    return .loadFile(name: name)
  }

  private static func decodePlayer (buffer: inout ByteBuffer) -> Outbound? {
    guard let speed = buffer.readUInt8() else {
      print("ERROR: The 'player' message doesn't have 'speed' field")
      return nil
    }
    guard let recordMode = buffer.readRecordMode() else {
      print("ERROR: The 'player' message doesn't have 'recordMode' field")
      return nil
    }
    guard let timestamp = buffer.readUInt32() else {
      print("ERROR: The 'player' message doesn't have 'timestamp' field")
      return nil
    }
    return .player(speed: speed, recordMode: recordMode, timestamp: timestamp)
  }

  private static func decodeRecord (buffer: inout ByteBuffer) -> Outbound? {
    guard let recordMode = buffer.readRecordMode() else {
      print("ERROR: The 'record' message doesn't have 'record mode' field")
      return nil
    }
    return .record(recordMode: recordMode)
  }

  private static func decodeDevicesRequest (buffer: inout ByteBuffer) -> Outbound? {
    return .devicesRequest
  }

  private static func decodeReset (buffer: inout ByteBuffer) -> Outbound? {
    return .reset
  }

  private static func decodeEthernetNextOrPrevious (buffer: inout ByteBuffer) -> Outbound? {
    guard let next = buffer.readUInt8() else {
      print("ERROR: The 'ethernetNextOrPrevious' message doesn't have 'next' field")
      return nil
    }
    return .ethernetNextOrPrevious(next: next == 0)
  }

  private static func decodeEthernetApply (buffer: inout ByteBuffer) -> Outbound? {
    return .ethernetApply
  }

  private static func decodeEthernetFinishAsk (buffer: inout ByteBuffer) -> Outbound? {
    return .ethernetFinishAsk
  }

  private static func decodeGenericResponse (buffer: inout ByteBuffer) -> Outbound? {
    guard let request = buffer.readUInt8() else {
      print("ERROR: The 'genericResponse' message doesn't have 'request' field")
      return nil
    }
    return .genericResponse(request: request)
  }

  private static func decodeAuthenticate (buffer: inout ByteBuffer) -> Outbound? {
    guard let deviceType = buffer.readDeviceType() else {
      print("ERROR: The 'authenticate' message doesn't have 'device' field")
      return nil
    }
    guard let code = buffer.ext_readBytes() else {
      print("ERROR: The 'authenticate' message doesn't have 'code' field")
      return nil
    }
    guard let name = buffer.readString() else {
      print("ERROR: The 'authenticate' message doesn't have 'name' field")
      return nil
    }
    guard let version = buffer.readUInt8() else {
      print("ERROR: The 'authenticate' message doesn't have 'version' field")
      return nil
    }
    return .authenticate(device: deviceType, code: code, name: name, version: version)
  }

  typealias InboundIn = ByteBuffer
  typealias InboundOut = Outbound

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    switch decode(data) {
    case .success(let outbound):
      context.fireChannelRead(wrapInboundOut(outbound))
    case .failure(let error):
      context.fireErrorCaught(error)
    }
  }

  func decode (_ data: NIOAny) -> Result<Outbound, Sm02Error> {
    var buffer = unwrapInboundIn(data)
    guard let tag = buffer.readUInt8() else {
      return .failure(.decodingOutboundFail("The message doesn't have a tag"))
    }
    guard let decoder = ByteBufferToOutboundDecoder.decoders[tag] else {
      return .failure(.decodingOutboundFail("The message doen't have a decoder for the tag - '\(tag)'"))
    }
    guard let status = buffer.readUInt8() else {
      return .failure(.decodingOutboundFail("The message doesn't have status"))
    }
    if status != 0 {
      return .failure(.decodingOutboundFail("The message has invalid request status. Should be 0, but it is - '\(status)'"))
    }
    guard let outbound = decoder(&buffer) else {
      return .failure(.decodingOutboundFail("Decoding request with tag '\(tag)' was failed"))
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

  mutating func ext_readBytes () -> [UInt8]? {
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

  mutating func readRecordMode () -> RecordMode? {
    return readUInt8().flatMap {
        return RecordMode(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readPersonType () -> PersonType? {
    return readUInt8().flatMap {
        return PersonType(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readTimerMode () -> TimerMode? {
    return readUInt8().flatMap {
        return TimerMode(rawValue: $0)
    }
  }
}

extension ByteBuffer {

  mutating func readCamera () -> Camera? {
    guard let name = readString() else {
      return nil
    }
    guard let target = readString() else {
      return nil
    }
    return Camera(name: name, target: target)
  }
}
