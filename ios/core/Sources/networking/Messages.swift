
public enum Weapon: UInt8 {

  case foil = 0x01
  case epee = 0x02
  case sabre = 0x03
}

public enum FlagState: UInt8 {

  case none = 0x00
  case white = 0x01
  case color = 0x02
}

public enum TimerState: UInt8 {

  case suspended = 0x00
  case running = 0x01
}

public enum DeviceType: UInt8 {

  case sm02 = 0x01
  case remoteControl = 0x02
  case camera = 0x03
  case repeater = 0x04
  case reserveReferee = 0x06
}

public enum StatusCard: UInt8 {

  case none = 0x01
  case yellow = 0x02
  case red = 0x03
  case black = 0x04
  case passiveNone = 0x05
  case passiveYellow = 0x06
  case passiveRed = 0x07
  case passiveBlack = 0x08
}

public enum Decision: UInt8 {

  case notAcknowledge = 0x00
  case acknowledge = 0x01
}

public enum RecordMode: UInt8 {

  case stop = 0x00
  case play = 0x01
  case pause = 0x02
}

public enum PersonType: UInt8 {

  case none = 0x00
  case left = 0x01
  case right = 0x02
  case referee = 0x03
}

public enum TimerMode: UInt8 {

  case main = 0x00
  case pause = 0x01
  case medicine = 0x02
}

public enum AuthenticationStatus: UInt8 {

  case success = 0x01
  case wrongAuthenticationCode = 0x81
  case alreadyRegistered = 0x82
}

public struct Device {

  public let name: String
  public let type: DeviceType

  public init (name: String, type: DeviceType) {
    self.name = name
    self.type = type
  }
}

public struct Side {

  public let score: UInt8
  public let card: StatusCard
  public let name: String

  public init (score: UInt8, card: StatusCard, name: String) {
    self.score = score
    self.card = card
    self.name = name
  }
}

public struct Camera {

  public let name: String
  public let target: String

  public init (name: String, target: String) {
    self.name = name
    self.target = target
  }
}

public enum Inbound {

  case tock
  case broadcast(weapon: Weapon, left: FlagState, right: FlagState, timer: UInt32, timerState: TimerState)
  case deviceList(devices: [Device])
  case ethernetDisplay(period: UInt8, time: UInt32, left: Side, right: Side)
  case fightResult(result: Decision)
  case passiveMax
  case pauseFinished
  case videoReady(name: String)
  case videoReceived
  case authentication(status: AuthenticationStatus)
  case genericResponse(request: UInt8)
}

public enum Outbound {

  case setName(person: PersonType, name: String)
  case setScore(person: PersonType, score: UInt8)
  case setCard(person: PersonType, status: StatusCard)
  case setPriority(person: PersonType)
  case setPeriod(period: UInt8)
  case setWeapon(weapon: Weapon)
  case setTimer(time: UInt32, mode: TimerMode)
  case startTimer(state: TimerState)
  case swap
  case visibility(video: Bool, photo: Bool, passive: Bool, country: Bool)
  case videoCounters(left: UInt8, right: UInt8)
  case disconnect
  case passiveTimer(shown: Bool, locked: Bool, defaultMilliseconds: UInt32)
  case setDefaultTime(time: UInt32)
  case setCompetition(name: String)
  case videoRoutes(cameras: [Camera])
  case loadFile(name: String)
  case player(speed: UInt8, recordMode: RecordMode, timestamp: UInt32)
  case record(recordMode: RecordMode)
  case devicesRequest
  case reset
  case ethernetNextOrPrevious(next: Bool)
  case ethernetApply
  case ethernetFinishAsk
  case tick
  case authenticate(device: DeviceType, code: [UInt8], name: String, version: UInt8)
  case genericResponse(request: UInt8)
}
