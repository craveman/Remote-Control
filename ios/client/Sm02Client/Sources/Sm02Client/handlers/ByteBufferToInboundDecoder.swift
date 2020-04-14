
import Foundation
import NIO
import NIOExtras


final class ByteBufferToInboundDecoder: ChannelInboundHandler {

  private typealias Decoder = (UInt8, inout ByteBuffer) -> Inbound?
  private static let decoders: [UInt8: Decoder] = [
    0x0B: decodeBroadcast,
    0x1A: decodeDeviceList,
    0x2A: decodeVideoReplaysList,
    0x21: decodeEthernetDisplay,
    0x22: decodeFightResult,
    0x11: decodePassiveMax,
    0x12: decodePauseFinished,
    0x1B: decodeVideoReady,
    0x1C: decodeVideoReceived,
    0x24: decodeAuthentication,
    0x66: decodeCameraIsOnline,
    0xAA: decodeGenericResponse,
  ]

  private static func decodeBroadcast (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
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

  private static func decodeDeviceList (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
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
  
  private static func decodeVideoReplaysList (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
//    guard let jsonLength = buffer.readUInt8() else {
//      print("ERROR: The 'decodeVideoReplaysList' message doesn't have 'count' field")
//      return nil
//    }

    var names: [String] = []
    guard let jsonString = buffer.readString() else {
      print("ERROR: The 'decodeVideoReplaysList' message doesn't have 'json[\"\"]' field", index)
      return nil
    }
    
    guard let json = try? JSONSerialization.jsonObject(with: jsonString.data(using: .utf8)!, options: []) else {
      print("ERROR: The 'decodeVideoReplaysList' message failed to parse json  from \(jsonString)")
      return nil
    }
    
    guard let list = json as? [String] else {
      print("ERROR: The 'decodeVideoReplaysList' message failed to parse json as list of strings from \(jsonString)")
      return nil
    }
    names.append(contentsOf: list)
    
    return .videoList(names: names)
  }

  private static func decodeEthernetDisplay (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
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

  private static func decodeFightResult (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    guard let decision = buffer.readDecision() else {
      print("ERROR: The 'fightResult' message doesn't have 'result' field")
      return nil
    }
    return .fightResult(result: decision)
  }

  private static func decodePassiveMax (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    return .passiveMax
  }

  private static func decodeCameraIsOnline (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    return .cameraOnline
  }

  private static func decodePauseFinished (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    return .pauseFinished
  }

  private static func decodeVideoReady (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    guard let name = buffer.readString() else {
      print("ERROR: The 'videoReady' message doesn't have 'name' field")
      return nil
    }
    return .videoReady(name: name)
  }

  private static func decodeVideoReceived (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    return .videoReceived
  }

  private static func decodeAuthentication (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    guard let authenticationStatus = AuthenticationStatus(rawValue: status) else {
      print("ERROR: The 'authentication' message doesn't have 'status' field")
      return nil
    }
    return .authentication(status: authenticationStatus)
  }

  private static func decodeGenericResponse (status: UInt8, buffer: inout ByteBuffer) -> Inbound? {
    guard let request = buffer.readUInt8() else {
      print("ERROR: The 'genericResponse' message doesn't have 'request' field")
      return nil
    }
    return .genericResponse(status: status, request: request)
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
      return .failure(.parsingdError("The message doesn't have a decoder for the tag - '\(tag)'"))
    }
    guard let status = buffer.readUInt8() else {
      return .failure(.parsingdError("The message '\(tag)' doesn't have status"))
    }
    guard let outbound = decoder(status, &buffer) else {
      return .failure(.parsingdError("The message '\(tag)' couldn't be decoded"))
    }
    return .success(outbound)
  }

  public func channelReadComplete (context: ChannelHandlerContext) {
    context.flush()
  }

  public func errorCaught (context: ChannelHandlerContext, error: Error) {
    print("ERROR: during channel handling - \(error)")
    // don't close the connection after error
    // context.close(promise: nil)
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
      let one = UInt32(array[3])
      let two = UInt32(array[2]) << 8
      let three = UInt32(array[1]) << 16
      let four = UInt32(array[0]) << 24

      return four + three + two + one
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

