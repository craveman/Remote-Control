//
//  RemoteService.swift
//  RemoteControl
//
//  Created by Artem Labazin on 14.11.2019.
//  Copyright © 2019 Artem Labazin, Sergei Andreev. All rights reserved.
//

import Foundation.NSNetServices
import struct Foundation.UUID
import class Foundation.NSCalendar
import typealias Foundation.Published

import class Foundation.NSTimer.Timer
import class Dispatch.DispatchQueue
import class Combine.AnyCancellable

import Sm02Client

import struct NIO.TimeAmount

@_exported import class Sm02Client.RemoteAddress
@_exported import class Sm02Client.Sm02Lookup

@_exported import enum Sm02Client.ConnectionEvent
@_exported import enum Sm02Client.ConnectionError

@_exported import enum Sm02Client.AuthenticationStatus
@_exported import enum Sm02Client.Decision
@_exported import enum Sm02Client.DeviceType
@_exported import enum Sm02Client.FlagState
@_exported import enum Sm02Client.Inbound
@_exported import enum Sm02Client.Outbound
@_exported import enum Sm02Client.PersonType
@_exported import enum Sm02Client.RecordMode
@_exported import enum Sm02Client.StatusCard
@_exported import enum Sm02Client.TimerMode
@_exported import enum Sm02Client.TimerState
@_exported import enum Sm02Client.Weapon
@_exported import struct Sm02Client.CompetitionState
@_exported import struct Sm02Client.FightState


fileprivate func log(_ items: Any...) {
//  print("RemoteService:log: ", items)
}

final class RemoteService {
  
  static let shared = RemoteService()
  static let SYNC_INTERVAL = 0.25
  static let PING_INTERVAL = 2.01
  
  let lookup = LookupManagement()
  let connection = ConnectionManagement()
  let persons = PersonsManagement()
  let competition = CompetitionManagement()
  let timer = TimerManagement()
  let video = VideoManagement()
  let display = DisplayManagement()
  
  private init() {
    // noop
  }
  
  func devicesRequest () {
    let outbound = Outbound.devicesRequest
    Sm02.send(message: outbound)
  }
  
  func ethernetNextOrPrevious (next: Bool) {
    let outbound = Outbound.ethernetNextOrPrevious(next: next)
    Sm02.send(message: outbound)
  }
  
  func ethernetApply () {
    let outbound = Outbound.ethernetApply
    Sm02.send(message: outbound)
  }
  
  func ethernetFinishAsk () {
    let outbound = Outbound.ethernetFinishAsk
    Sm02.send(message: outbound)
  }
  
  @discardableResult
  func on (event handler: @escaping EventHandler) -> UUID {
    return Sm02.on(event: handler)
  }
  
  @discardableResult
  func remove (event uuid: UUID) -> Bool {
    return Sm02.remove(eventHandler: uuid)
  }
  
  enum SM_Data_Keys: String {
    case id = "ID"
    case rc = "RC"
  }
  
  enum SM_RC_States: String {
    case isBusy = "YES"
    case isVacant = "NO"
  }
  
  
  final class LookupManagement {
    private let UDP_LOOKUP_MODE = false
    private let BONJOUR_LOOKUP_MODE = true
    let SM_DOMAIN = "local." // or ""
    let SM_TYPE = "_ip_top._tcp."
    let SM_NAME = "InspirationPoint"
    private var nsb: NetServiceBrowser?
    private var nsbdel:SMNetBrowserDelegate?
    private var bonjourRefreshTimerStopper: (() -> Void)? = nil
    
    @Published
    private(set) var isStarted: Bool = false
    
    @Published
    private(set) var remoteAddresses: [LanConfigReaderOption] = []
    
    init () {
      registerNetServices()
    }
    
    deinit {
      destroyNetServices()
    }
    
    func refreshBonjor() -> Void {
      if isStarted {
        nsb?.stop()
        nsb?.searchForServices(ofType: SM_TYPE, inDomain: SM_DOMAIN)
        withDelay({
          self.nsb?.stop();
        }, 2 * PING_INTERVAL)
      }
    }
    
    func start (listen port: Int = Sm02Lookup.DEFAULT_SM_UDP_PORT) {
      
      //      nsb?.searchForServices(ofType: "_companion-link._tcp.", inDomain: "local.") // check for devices
      if UDP_LOOKUP_MODE {
        DispatchQueue.global(qos: .default).async {
          Sm02Lookup.start(listen: port)
        }
      }
      
      isStarted = true
      refreshBonjor()
    }
    
    func stop (_ clean: Bool = true) {
      if BONJOUR_LOOKUP_MODE {
        nsb?.stop()
      }
      if UDP_LOOKUP_MODE {
        Sm02Lookup.stop()
      }
      if clean {
        remoteAddresses.removeAll()
      }
      
      isStarted = false
    }
    
    private func onNetServicesResolved(_ netService: NetService, _ action: SMNetBrowserAction) {
      let addr = netService.addresses?.map({self.nameForAddress($0)}) ?? [];
     
      if let data = netService.txtRecordData(), netService.name.hasPrefix(self.SM_NAME), let ip = addr.first {
       
        let dict = NetService.dictionary(fromTXTRecord: data)
        //          print("data: ", dict, String(data: data, encoding: .utf8) ?? "<no_data>")
        var name = "\(ip)"
        if let id = dict[SM_Data_Keys.id.rawValue] {
          name = String(data: id, encoding: .utf8) ?? ""
        }
        
        var hasRC = false
        if let rc = dict[SM_Data_Keys.rc.rawValue] {
          hasRC = String(data: rc, encoding: .utf8) != SM_RC_States.isVacant.rawValue;
          log(action)
          let remote = RemoteAddress(ssid: "", ip: ip, code: [0,0,0,0,0])
          self.handleSmAddrLost(addr: remote)
          if (action == .serviceFound) {
            self.handleSmAddrFound(addr: remote, name: name, busy: hasRC)
          }
          
        } else {
          log("Non RC capable \(self.SM_NAME) service was skipped")
        }
        
      } else {
        log("Not suitable service was skipped")
      }
      //        print("domn: ", netService.domain)
      //        print("type: ", netService.type)
      //        print("host: ", netService.hostName ?? "<no_host_name>")
      //        print("addr: ", addr)
      //        print("name: ", netService.name)
      //        print("port: ", netService.port)
      
    }
    
    private func handleSmAddrFound(addr: RemoteAddress, name: String, busy: Bool = false) {
      self.remoteAddresses.append((address: addr, busy: busy, name: name))
    }
    
    private func handleSmAddrLost(addr: RemoteAddress) {
      self.remoteAddresses = self.remoteAddresses.filter({ $0.address != addr })
      
    }
    
    private func registerNetServices() {
      
      log("registerNetServices")
      
      if UDP_LOOKUP_MODE {
        Sm02Lookup.on(server: { [unowned self] (remoteAddress) in
          self.handleSmAddrLost(addr: remoteAddress)
          self.handleSmAddrFound(addr: remoteAddress, name: "\(remoteAddress.ip)")
          print("RemoteService.LookupManagement - INFO: the new added remote address \(remoteAddress)")
        })
      }
      
      if BONJOUR_LOOKUP_MODE {
        nsb = NetServiceBrowser()
        nsbdel = SMNetBrowserDelegate(onNetServicesResolved) //see bellow
        nsb?.delegate = nsbdel
        let timer = Timer.scheduledTimer(withTimeInterval: TimeInterval(3 * PING_INTERVAL), repeats: true) {timer in
          self.refreshBonjor()
        }
        bonjourRefreshTimerStopper = {
          timer.invalidate();
        }
      }
    }
    
    private func destroyNetServices() {
      
      log("stopNetServices")
      if (bonjourRefreshTimerStopper != nil) {
        bonjourRefreshTimerStopper!()
      }
      stop()
    }
    
    private func nameForAddress(_ address: Data) -> String {
      var host = [CChar](repeating: 0, count: Int(NI_MAXHOST))
      let err = address.withUnsafeBytes { buf -> Int32 in
        let sa = buf.baseAddress!.assumingMemoryBound(to: sockaddr.self)
        let saLen = socklen_t(buf.count)
        return getnameinfo(sa, saLen, &host, socklen_t(host.count), nil, 0, NI_NUMERICHOST | NI_NUMERICSERV)
      }
      guard err == 0 else { return "?" }
      return String(cString: host)
    }
  }
  
  final class ConnectionManagement {
    
    @Published
    private(set) var isConnected: Bool = false
    
    @Published
    private(set) var isAuthenticated: Bool = false
    
    @Published
    private(set) var address: RemoteAddress? = nil
    
    @Published
    private(set) var isAlive: Bool = false
    
    fileprivate var subs: [AnyCancellable] = []
    
    private func setAliveChecker() {
//        self.isAlive = true
    }
    
    init () {
      Sm02.on(message: { [unowned self] (inbound) in
        if case .broadcast(_, _, _, _, _) = inbound {
//          print("<<<<<<<<<<<<<<<<<< broadcast")
          self.setAliveChecker()
        }
        
        if case .authentication(.success) = inbound {
          self.isAuthenticated = true
          self.setAliveChecker()
        }
        if case .quit = inbound {
          print("quit")
          self.disconnect()
        }
      })
      Sm02.on(event: { [unowned self] (event) in
//        print(">>>>>>>>>>>>>>>>>>>>>>>>>>", event)
        switch event {
        case .connected:
          self.isConnected = true
          self.setAliveChecker()
        case .disconnected:
          self.isConnected = false
        default:
          break
        }
      })
      let temp = [
        $address.on(change: { [unowned self] (update) in
          if update === RemoteAddress.empty {
            self.isAuthenticated = false
          }
        })
      ]
      
      self.subs = temp
    }
    
    func connect (to remote: RemoteAddress) -> Result<AuthenticationStatus, Error> {
      let result = Sm02.connect(to: remote)
      if case .success(AuthenticationStatus.success) = result {
        address = remote
      }
      return result
    }
    
    func disconnect (temporary: Bool = false) {
      Sm02.disconnect()
      if temporary == false {
        forget()
      }
      isConnected = false
    }
    
    func forget () {
      address = RemoteAddress.empty
    }
  }
  
  final class PersonsManagement {
    
    let left = Person(type: .left)
    let right = Person(type: .right)
    let referee = Person(type: .referee)
    let none = Person(type: .none)
    
    subscript (type: PersonType) -> Person {
      switch type {
      case .left:
        return left
      case .right:
        return right
      case .referee:
        return referee
      case .none:
        return none
      }
    }
    
    private func send(message outbound: Outbound) {
      log("PersonsManagement send", outbound)
      Sm02.send(message: outbound)
    }
    
    func resetPriority (updateRemote: Bool = true) {
      Person.priorityType = .none
      guard updateRemote else {
        log("Person.priorityType reset was not send")
        return
      }
      let outbound = Outbound.setPriority(person: .none)
      send(message: outbound)
    }
    
    func resetNames () {
      left.name = ""
      right.name = ""
    }
    
    func confirmNames () {
      let outbound = Outbound.confirmNames
      send(message: outbound)
    }
    
    final class Person {
      fileprivate func sync(competition data: CompetitionState.Fighter, priority: Bool) {
        self.score = UInt8(data.score)
        if priority {
          Person.priorityType = self.type
        }
        self.name = data.name
        if data.redCardCount > 0 {
          self.card = .red
        } else if data.yellowCardCount > 0 {
          self.card = .yellow
        } else {
          self.card = .none
        }
        self.passiveCard = .none
      }
      
      fileprivate func sync(fight data: FightState.FighterData, priority: Bool) {
        self.score = UInt8(data.matchScore)
        if priority {
          Person.priorityType = self.type
        }
        self.name = data.matchName
        switch data.matchCard {
        case .black:
          self.card = .black
        case .red:
          self.card = .red
        case .yellow:
          self.card = .yellow
        default:
          self.card = .none
        }
        // select P-Card
        log("sync(fight data", type, data.matchPassiveCard )
        switch data.matchPassiveCard {
        case .black:
          self.passiveCard = .black
        case .red:
          self.passiveCard = .red
        case .yellow:
          self.passiveCard = .yellow
        default:
          self.passiveCard = .passiveNone
        }
      }
      
      private func send(message outbound: Outbound) {
        guard !isSyncing else {
          log("Message send canceled (isSyncing): \(outbound)")
          return
        }
        log("Person update Message send: \(outbound)")
        Sm02.send(message: outbound)
      }
      private var isSyncing: Bool = false
      fileprivate static var priorityType: PersonType = .none
      fileprivate let type: PersonType
      
      @Published
      var name = ""
      
      @Published
      var card: StatusCard = .none
      
      @Published
      var passiveCard: StatusCard = .none
      
      @Published
      var score: UInt8 = 0
      
      var isPriority: Bool { Person.priorityType == type }
      
      fileprivate var subs: [AnyCancellable] = []
      
      fileprivate init (type: PersonType) {
        self.type = type
      }
      
      func setScore(_ score: UInt8) -> Void {
        let outbound = Outbound.setScore(person: type, score: score)
        self.send(message: outbound)
        self.score = score
      }
      
      func setName(_ name: String) -> Void {
        let outbound = Outbound.setName(person: type, name: name)
        self.send(message: outbound)
        self.name = name
      }
      
      func setCard(_ card: StatusCard) -> Void {
        switch card {
        case .none, .black, .yellow, .red:
          self.card = card
          break
        case .passiveNone, .passiveBlack, .passiveYellow, .passiveRed:
          self.passiveCard = card
          break
        }
        
        let outbound = Outbound.setCard(person: type, status: card)
        send(message: outbound)
      }
      
      func setPriority () {
        let outbound = Outbound.setPriority(person: type)
        send(message: outbound)
        Person.priorityType = type
      }
    }
  }
  
  final class CompetitionManagement {
    private var raceConditionLock = false
    let flags = FlagsManagement()
    
    @Published
    var name = ""
    
    @Published
    var cyranoWorks = false
    
    @Published
    private(set) var cyranoOption = ""
    
    @Published
    var cameraIsOnline = false
    
    @Published
    var weapon: Weapon = .none
    
    @Published
    private(set) var period: UInt8 = 0
    
    @Published
    var periodTime: UInt32 = 0
    
    @Published
    private(set) var individualFight = true
    @Published
    private(set) var teamFight = false
    
    @Published
    private(set) var fightStatus: Decision = .continueFight
    
    @Published
    private(set) var state: FightState? {
      didSet {
        log("FightState was set")
        guard let input = state else {
          return
        }
        
        period = UInt8(input.matchCurrentPeriod)
        
        
        individualFight = input.ethernetCompetitionType == .individual
        teamFight = input.ethernetCompetitionType == .team
        
        PersonsManagement.Person.priorityType = .none
        RemoteService.shared.persons.left.sync(fight: input.matchLeftFighterData, priority: input.matchPriority == .left)
        //        print("sync RemoteService.shared.persons.left.passiveCard \(RemoteService.shared.persons.left.passiveCard)")
        RemoteService.shared.persons.right.sync(fight: input.matchRightFighterData, priority: input.matchPriority == .right)
        //        print("sync RemoteService.shared.persons.right.passiveCard \(RemoteService.shared.persons.right.passiveCard)")
        RemoteService.shared.video.replay.syncCounters(leftCount: UInt8(input.matchVideoLeft), rightCount: UInt8(input.matchVideoRight))
        RemoteService.shared.timer.syncTime(time: UInt32(input.matchCurrentTime))
        print("sync RemoteService.shared.timer.time ", RemoteService.shared.timer.time)
      }
    }
    
    @Published
    private(set) var status: CompetitionState?
    
    fileprivate var subs: [AnyCancellable] = []
    
    fileprivate init () {
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .broadcast(weapon, _, _, _, _) = inbound else {
          return
        }
        guard self.raceConditionLock == false else {
          return
        }
        self.weapon = weapon
      })
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .additionalState(camera, cyrano) = inbound else {
          return
        }
        if self.cameraIsOnline != camera {
          self.cameraIsOnline = camera
          log("camera: \(camera)")
        }
        if self.cyranoWorks != cyrano {
          self.cyranoWorks = cyrano
          log("cyrano: \(cyrano)")
        }
        
      })
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .fightResult(result) = inbound else {
          return
        }
        self.fightStatus = result
      })
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .setFightCommand(state) = inbound else {
          return
        }
        self.state = state
      })
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .competition(state) = inbound else {
          return
        }
        self.status = state
        print("RS \(self.status)")
        guard let fight = self.status else {
          return
        }
        
        self.cyranoOption = getFightName(left: fight.left.name, right: fight.right.name)
      })
      
      let temp = [
        $name.on(change: { update in
          let outbound = Outbound.setCompetition(name: update)
          Sm02.send(message: outbound)
        }),
        $weapon.on(change: { update in
          // Sm02 could sometimes reject '.none'
          guard self.weapon != .none else {
            return
          }
          self.raceConditionLock = true
          
          let outbound = Outbound.setWeapon(weapon: update)
          Sm02.send(message: outbound)
          
          Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
            self.raceConditionLock = false
          }
        }),
        $periodTime.on(change: { update in
          let outbound = Outbound.setDefaultTime(time: update)
          Sm02.send(message: outbound)
        })]
      self.subs = temp
    }
    
    func reset () {
      log("competition reset")
      let outbound = Outbound.reset
      Sm02.send(message: outbound)
    }
    
    func swap () {
      let outbound = Outbound.swap
      Sm02.send(message: outbound)
    }
    
    func setPeriod(_ next: UInt8) -> Void {
      let outbound = Outbound.setPeriod(period: next)
      Sm02.send(message: outbound)
      period = next
    }
    
    func cyranoApply () {
      print("cyranoApply")
      RemoteService.shared.ethernetApply()
      
      guard let next = status else {
        log("cyranoApply failed to find status")
        return
      }
      
      period = UInt8(next.period)
      
      individualFight = next.type == .individual
      teamFight = next.type == .team
      
      PersonsManagement.Person.priorityType = .none
      
      RemoteService.shared.persons.left.sync(competition: next.left, priority: next.priority == .left)
      RemoteService.shared.persons.right.sync(competition: next.right, priority: next.priority == .right)
      
    }
    
    final class FlagsManagement {
      
      @Published
      private(set) var left: FlagState = .none
      
      @Published
      private(set) var right: FlagState = .none
      
      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          guard case let .broadcast(_, left, right, _, _) = inbound else {
            return
          }
          if self.left != left {
            self.left = left
          }
          if self.right != right {
            self.right = right
          }
        })
      }
    }
  }
  
  final class TimerManagement {
    private var raceConditionLock = false
    let passive = PassiveManagement()
    
    @Published
    private(set) var time: UInt32 = 0
    
    @Published
    private(set) var mode: TimerMode = .main
    
    @Published
    private(set) var state: TimerState = .suspended
    
    @Published
    private(set) var isPauseFinished = false
    
    fileprivate init () {
      Sm02.on(message: { [unowned self] (inbound) in
        switch inbound {
        case .pauseFinished:
          self.isPauseFinished = true
          log("pauseFinished: \(self.isPauseFinished)")
        case let .broadcast(_, _, _, timer, timerState):
          //          log("\(timer), \(timerState)")
          if self.time != timer {
            self.time = timer
          }
          if self.state != timerState {
            if self.raceConditionLock {
              return
            }
            log("toggle state")
            self.state = timerState
            if self.state == .running && (self.mode == .medicine || self.mode == .pause) && self.isPauseFinished {
              self.isPauseFinished = false
              log("pauseFinished set: \(self.isPauseFinished)")
            }
          }
        default:
          break
        }
      })
    }
    
    func syncTime(time: UInt32) {
      self.time = time
    }
    
    func set (time: TimeAmount, mode: TimerMode) {
      let milliseconds = max(1, UInt32(time.nanoseconds / 1_000_000)) - 1
      let outbound = Outbound.setTimer(time: milliseconds, mode: mode)
      Sm02.send(message: outbound)
      
      
      self.mode = mode
      self.raceLock()
    }
    
    private func raceLock() {
      self.raceConditionLock = true
      Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
        self.raceConditionLock = false
      }
    }
    
    func pause (_ time: TimeAmount, mode: TimerMode) {
      set(time: time, mode: mode)
    }
    
    func start () {
      let outbound = Outbound.startTimer(state: .running)
      Sm02.send(message: outbound)
      self.raceLock()
    }
    
    func stop () {
      let outbound = Outbound.startTimer(state: .suspended)
      Sm02.send(message: outbound)
      self.raceLock()
    }
    
    final class PassiveManagement {
      public static var DEFAULT_PASSIVE_TIMER: UInt32 = 60000
      
      @Published
      var isVisible = false
      
      @Published
      var isBlocked = false {
        didSet {
          if (isBlocked) {
            Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
              self.isBlocked = false
            }
          }
        }
      }
      
      @Published
      var defaultMilliseconds: UInt32 = DEFAULT_PASSIVE_TIMER
      
      @Published
      private(set) var isMaxTimerReached = false
      
      fileprivate var subs: [AnyCancellable] = []
      
      fileprivate init () {
        
        Sm02.on(message: { [unowned self] (inbound) in
          switch inbound {
          case .passiveMax:
            self.isMaxTimerReached = true
            Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
              self.isMaxTimerReached = false
            }
          default:
            return
          }
        })
        let temp = [
          $isVisible.on(change: { [unowned self] (update) in
            let outbound = Outbound.passiveTimer(
              shown: update,
              locked: self.isBlocked,
              defaultMilliseconds: self.defaultMilliseconds
            )
            Sm02.send(message: outbound)
          }),
          
          $isBlocked.on(change: { [unowned self] (update) in
            // send only when locked
            guard update == true else {
              return
            }
            
            let outbound = Outbound.passiveTimer(
              shown: self.isVisible,
              locked: update,
              defaultMilliseconds: self.defaultMilliseconds
            )
            Sm02.send(message: outbound)
          }),
          
          $defaultMilliseconds.on(change: { [unowned self] (update) in
            let outbound = Outbound.passiveTimer(
              shown: self.isVisible,
              locked: self.isBlocked,
              defaultMilliseconds: update
            )
            Sm02.send(message: outbound)
          })
        ]
        self.subs = temp
      }
    }
  }
  
  final class VideoManagement {
    
    let replay = VideoReplayManagement()
    let player = VideoPlayerManagement()
    
    @Published
    var recordMode: RecordMode = .stop
    
    func cut() -> Void {
      let outbound = Outbound.record(recordMode: .pause)
      Sm02.send(message: outbound)
      //
      //      withDelay({
      //        self.replay.doVideoReady()
      //      })
    }
    
    @Published
    var routes: [Camera] = []
    
    fileprivate var subs: [AnyCancellable] = []
    
    fileprivate init () {
      let temp = [
        $recordMode.on(change: { update in
          let outbound = Outbound.record(recordMode: update)
          Sm02.send(message: outbound)
        }),
        $routes.on(change: { update in
          let outbound = Outbound.videoRoutes(cameras: update)
          Sm02.send(message: outbound)
        })
      ]
      self.subs = temp
    }
    
    func upload (to fileName: String) {
      let outbound = Outbound.loadFile(name: fileName)
      Sm02.send(message: outbound)
      //
      //      withDelay({
      //        self.replay.doReceived()
      //      })
    }
    
    final class VideoPlayerManagement {
      
      @Published
      var speed: UInt8 = 10
      
      @Published
      private(set) var mode: RecordMode = .stop
      
      @Published
      private(set) var timestamp: UInt32 = 0
      
      fileprivate var subs: [AnyCancellable] = []
      private var comboLock = false
      
      fileprivate init () {
        let temp = [
          $speed.on(change: { [unowned self] update in
            guard !self.comboLock else {
              return
            }
            let outbound = Outbound.player(speed: update, recordMode: self.mode, timestamp: self.timestamp)
            Sm02.send(message: outbound)
          }),
          $mode.on(change: { [unowned self] update in
            guard !self.comboLock else {
              return
            }
            let outbound = Outbound.player(speed: self.speed, recordMode: update, timestamp: self.timestamp)
            Sm02.send(message: outbound)
          })
        ]
        self.subs = temp
      }
      
      func goto (_ timestamp: UInt32) {
        let outbound = Outbound.player(speed: speed, recordMode: mode, timestamp: timestamp)
        Sm02.send(message: outbound)
        self.timestamp = timestamp
      }
      
      func stop () {
        mode = .stop
      }
      
      func play () {
        mode = .play
      }
      
      func pause () {
        let temp = timestamp
        timestamp = 101
        mode = .pause
        Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
          self.timestamp = temp
        }
      }
      
      func standBy() {
        comboLock = true
        mode = .pause
        speed = 10
        goto(0)
        Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
          self.comboLock = false
        }
      }
    }
    
    final class VideoReplayManagement {
      
      private var isSyncing = false
      
      private func send(message outbound: Outbound) {
        guard !isSyncing else {
          return
        }
        print("VideoReplayManagement send", outbound)
        Sm02.send(message: outbound)
      }
      
      fileprivate func syncCounters(leftCount: UInt8?, rightCount: UInt8?) {
        if leftCount != nil {
          leftCounter = leftCount!
        }
        if rightCount != nil {
          rightCounter = rightCount!
        }
      }
      
      fileprivate func doReceived() {
        isReceived = true
      }
      
      public static var MAX_COUNTER: UInt8 = 2
      public static var DEFAULT_INIT_COUNTER: UInt8 = MAX_COUNTER
      
      func setCounter(left num: UInt8) {
        let outbound = Outbound.videoCounters(left: num, right: self.rightCounter)
        self.send(message: outbound)
        self.leftCounter = num
      }
      
      func setCounter(right num: UInt8) {
        let outbound = Outbound.videoCounters(left: self.leftCounter, right: num)
        self.send(message: outbound)
        self.rightCounter = num
      }
      
      func setCounters(left leftNum: UInt8, right rightNum: UInt8) {
        let outbound = Outbound.videoCounters(left: leftNum, right: rightNum)
        self.send(message: outbound)
        self.leftCounter = leftNum
        self.rightCounter = rightNum
      }
      
      func resetCounters() {
        self.leftCounter = VideoReplayManagement.DEFAULT_INIT_COUNTER
        self.rightCounter = VideoReplayManagement.DEFAULT_INIT_COUNTER
      }
      
      @Published
      var leftCounter: UInt8 = VideoReplayManagement.DEFAULT_INIT_COUNTER
      
      @Published
      var rightCounter: UInt8 = VideoReplayManagement.DEFAULT_INIT_COUNTER
      
      @Published
      private(set) var isReady = false
      
      //      @Published
      private(set) var recordsList: [String] = []
      
      @Published
      private(set) var isReceived = false
      
      fileprivate var subs: [AnyCancellable] = []
      private var counter = 0
      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          guard case let .videoReady(name) = inbound else {
            return
          }
          self.isReady = true
          self.recordsList.append(name)
        })
        Sm02.on(message: { [unowned self] (inbound) in
          guard case .videoReceived = inbound else {
            return
          }
          self.isReceived = true
          
          Timer.scheduledTimer(withTimeInterval: RemoteService.SYNC_INTERVAL, repeats: false) {[unowned self] _ in
            self.isReceived = false
          }
        })
        Sm02.on(message: { [unowned self] (inbound) in
          guard case let .videoList(list) = inbound else {
            return
          }
          self.recordsList.removeAll()
          guard let names = list else {
            log("Failed to parse list name Inbound (.videoList)")
            self.isReady = false
            return
          }
          
          self.recordsList.append(contentsOf: names)
          self.isReady = names.count > 0
        })
      }
      
      fileprivate func doVideoReady() {
        self.counter += 1
        self.isReady = true
        self.recordsList.append("\(self.counter)")
      }
      
      func stopLoading() -> Void {
        if (!isReceived) {
          Sm02.send(message: .stopReplayLoading)
        }
      }
      
      func clear() -> Void {
        self.recordsList.removeAll()
      }
      
      func refresh() -> Void {
        Sm02.send(message: .videoListRequest)
      }
      
      // TODO: add refresh of recordsList
    }
  }
  
  final class DisplayManagement {
    
    @Published
    var video = false
    
    @Published
    var photo = false
    
    @Published
    var passive = false
    
    @Published
    var country = false
    
    fileprivate var subs: [AnyCancellable] = []
    
    fileprivate init () {
      let temp = [
        $video.on(change: { [unowned self] (update) in
          let outbound = Outbound.visibility(
            video: update,
            photo: self.photo,
            passive: self.passive,
            country: self.country
          )
          Sm02.send(message: outbound)
        }),
        $photo.on(change: { [unowned self] (update) in
          let outbound = Outbound.visibility(
            video: self.video,
            photo: update,
            passive: self.passive,
            country: self.country
          )
          Sm02.send(message: outbound)
        }),
        $passive.on(change: { [unowned self] (update) in
          let outbound = Outbound.visibility(
            video: self.video,
            photo: self.photo,
            passive: update,
            country: self.country
          )
          Sm02.send(message: outbound)
        }),
        $country.on(change: { [unowned self] (update) in
          let outbound = Outbound.visibility(
            video: self.video,
            photo: self.photo,
            passive: self.passive,
            country: update
          )
          Sm02.send(message: outbound)
        })
      ]
      self.subs = temp
    }
  }
}
