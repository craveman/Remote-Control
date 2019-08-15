
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

public struct Device {

  let name: String
  let type: DeviceType
}

public struct Side {

  let score: UInt8
  let card: StatusCard
  let name: String
}

public struct Camera {

  let name: String
  let target: String
}

public enum Inbound {

  case broadcast(weapon: Weapon, left: FlagState, right: FlagState, timer: UInt32, timerState: TimerState)
  case deviceList(devices: [Device])
  case ethernetDisplay(period: UInt8, time: UInt32, left: Side, right: Side)
  case fightResult(result: Decision)
  case passiveMax
  case pauseFinished
  case ping
  case videoReady(name: String)
  case videoReceived
}

public enum Outbound {

  case setName(personType: PersonType, name: String)
  case setScore(personType: PersonType, score: UInt8)
  case setCard(personType: PersonType, statusCard: StatusCard)
  case setPriority(personType: PersonType)
  case setPeriod(period: UInt8)
  case setWeapon(weapon: Weapon)
  case setTimer(time: UInt32, mode: TimerMode)
  case startTimer(timerState: TimerState)
  case swap
  case visibility(video: Bool, photo: Bool, passice: Bool, country: Bool)
  case videoCounters(left: UInt8, right: UInt8)
  case hello(deviceType: DeviceType, name: String)
  case disconnect
  case passive(shown: Bool, locked: Bool, defaultMilliseconds: UInt32)
  case setDefaultTime(time: UInt32)
  case competition(name: String)
  case videoRoutes(cameras: [Camera])
  case loadFile(name: String)
  case player(speed: UInt8, recordMode: RecordMode, timestamp: UInt32)
  case record(recordMode: RecordMode)
  case devicesRequest
  case reset
  case ethernetNextOrPrevious(neext: Bool)
  case ethernetApply
  case ethernetFinishAsk
}
