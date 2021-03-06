
import struct Foundation.Date


public enum Weapon: UInt8 {

  case none = 0x00
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

  case continueFight = 0x00
  case stopFight = 0x01
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

public enum CompetitionType: String, Decodable {
  case team = "T"
  case individual = "I"
  case none = ""
}

public enum Priority: Int, Decodable {
  case left = 1
  case right = 2
  case none = 0
}

public struct FightState: Decodable {

  public let ethernetCompetitionType: CompetitionType
  public let ethernetLeftNation: String
  public let ethernetMatchNumber: String
  public let ethernetRightNation: String
  public let ethernetStatus: Status
  public let matchCurrentPeriod: Int
  public let matchCurrentTime: Int64
  // public let matchDate: Date
  public let matchPriority: Priority
  public let matchVideoLeft: Int
  public let matchVideoRight: Int
  public let matchLeftFighterData: FighterData
  public let matchRightFighterData: FighterData

  public enum Status: String, Decodable {
    case fencing = "F"
    case halt = "H"
    case pause = "P"
    case waiting = "W"
    case ending = "E"
    case none = ""
  }
  public struct FighterData: Decodable {

    public let matchCard: CardStatus
    public let matchId: String
    public let matchName: String
    public let matchPassiveCard: PassiveCardStatus
    public let matchScore: Int
    public let redCardCount: Int
    public let redPassiveCardCount: Int
    public let yellowCardCount: Int
    public let yellowPassiveCardCount: Int

    public enum CardStatus: String, Decodable {
      case none = "CardStatus_None"
      case yellow = "CardStatus_Yellow"
      case red = "CardStatus_Red"
      case black = "CardStatus_Black"
      case unknown = ""
    }
    public enum PassiveCardStatus: String, Decodable {
      case none = "CardPStatus_None"
      case yellow = "CardPStatus_Yellow"
      case red = "CardPStatus_Red"
      case black = "CardPStatus_Black"
      case unknown = ""
    }
  }
}

public struct CompetitionState {

  public let id: String
  public let phase: String
  public let matchNumber: Int
  public let period: Int
  public let timer: String
  public let type: CompetitionType
  public let priority: Priority
  public let left: Fighter
  public let right: Fighter

  public struct Fighter {

    public let id: String
    public let name: String
    public let nation: String
    public let score: Int
    public let status: String
    public let yellowCardCount: UInt
    public let redCardCount: UInt
  }
}

public enum Inbound {

  case broadcast(weapon: Weapon, left: FlagState, right: FlagState, timer: UInt32, timerState: TimerState)
  case quit
  case additionalState(isCamConnected: Bool, isEthernetEnabled: Bool)
  case deviceList(devices: [Device])
  case competition(state: CompetitionState)
  case fightResult(result: Decision)
  case passiveMax
  case pauseFinished
  case videoList(names: [String]?)
  case videoReady(name: String)
  case videoReceived
  case authentication(status: AuthenticationStatus)
  case genericResponse(status: UInt8 = 0x01, request: UInt8)
  case setFightCommand(state: FightState)
}

public enum Outbound {

  case setName(person: PersonType, name: String)
  case confirmNames
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
  case videoListRequest
  case stopReplayLoading
  case reset
  case ethernetNextOrPrevious(next: Bool)
  case ethernetApply
  case ethernetFinishAsk
  case authenticate(device: DeviceType, code: [UInt8], name: String, version: UInt8)
  case genericResponse(status: UInt8 = 0x01, request: UInt8)
}

public protocol Message: Hashable {

  var tag: UInt8 { get }
}

public extension Message {

  static func == (left: Self, right: Self) -> Bool {
    return left.tag == right.tag
  }

  func hash (into hasher: inout Hasher) {
    hasher.combine(tag)
  }
}

extension Inbound: Message {

  public var tag: UInt8 {
    switch self {
    case .broadcast:
      return 0x0B
    case .quit:
      return 0x0F
    case .deviceList:
      return 0x1A
    case .competition:
      return 0x21
    case .fightResult:
      return 0x22
    case .passiveMax:
      return 0x11
    case .pauseFinished:
      return 0x12
    case .videoList:
      return 0x2A
    case .videoReady:
      return 0x1B
    case .videoReceived:
      return 0x1C
    case .authentication:
      return 0x24
    case .additionalState:
        return 0x66
    case .genericResponse:
      return 0xAA
    case .setFightCommand:
      return 0x2B
    }
  }
}

extension Outbound: Message {

  public var tag: UInt8 {
    switch self {
    case .setName:
      return 0x01
    case .confirmNames:
      return 0x27
    case .setScore:
      return 0x03
    case .setCard:
      return 0x04
    case .setPriority:
      return 0x05
    case .setPeriod:
      return 0x06
    case .setWeapon:
      return 0x07
    case .setTimer:
      return 0x08
    case .startTimer:
      return 0x09
    case .swap:
      return 0x0A
    case .visibility:
      return 0x0C
    case .videoCounters:
      return 0x0D
    case .disconnect:
      return 0x0F
    case .passiveTimer:
      return 0x10
    case .setDefaultTime:
      return 0x13
    case .setCompetition:
      return 0x14
    case .videoRoutes:
      return 0x15
    case .loadFile:
      return 0x16
    case .stopReplayLoading:
      return 0x25
    case .player:
      return 0x17
    case .record:
      return 0x18
    case .devicesRequest:
      return 0x19
    case .videoListRequest:
      return 0x29
    case .reset:
      return 0x1D
    case .ethernetNextOrPrevious:
      return 0x1E
    case .ethernetApply:
      return 0x1F
    case .ethernetFinishAsk:
      return 0x20
    case .authenticate:
      return 0x23
    case .genericResponse:
      return 0xAA
    }
  }
}
