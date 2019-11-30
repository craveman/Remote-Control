//
//  RemoteService.swift
//  RemoteControl
//
//  Created by Artem Labazin on 14.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import struct Foundation.UUID
import Sm02Client

import struct NIO.TimeAmount

@_exported import class Sm02Client.RemoteAddress

@_exported import enum Sm02Client.ConnectionEvent
@_exported import enum Sm02Client.ConnectionError

@_exported import enum Sm02Client.Weapon
@_exported import enum Sm02Client.FlagState
@_exported import enum Sm02Client.TimerState
@_exported import enum Sm02Client.DeviceType
@_exported import enum Sm02Client.StatusCard
@_exported import enum Sm02Client.Decision
@_exported import enum Sm02Client.RecordMode
@_exported import enum Sm02Client.PersonType
@_exported import enum Sm02Client.TimerMode
@_exported import enum Sm02Client.AuthenticationStatus
@_exported import enum Sm02Client.Inbound
@_exported import enum Sm02Client.Outbound

final class RemoteService {

  static let shared = RemoteService()

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

  class ConnectionManagement {

    let addressProperty: ObservableProperty<RemoteAddress> = ObjectProperty<RemoteAddress>(RemoteAddress.empty)
    let isConnectedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
    let isAuthenticatedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)

    var isConnected: Bool { (isConnectedProperty as! PrimitiveProperty<Bool>).get() }
    var isAuthenticated: Bool { (isAuthenticatedProperty as! PrimitiveProperty<Bool>).get() }
    var address: RemoteAddress? {
      let value = (addressProperty as! ObjectProperty<RemoteAddress>).get()
      return Optional.some(value)
          .flatMap { $0.isEmpty() ? nil : $0 }
    }

    init () {
      Sm02.on(message: { [unowned self] (inbound) in
        if case .authentication(.success) = inbound {
          (self.isAuthenticatedProperty as! PrimitiveProperty<Bool>).set(true)
        }
      })
      Sm02.on(event: { [unowned self] (event) in
        switch event {
        case .connected:
          (self.isConnectedProperty as! PrimitiveProperty<Bool>).set(true)
        case .disconnected:
          (self.isConnectedProperty as! PrimitiveProperty<Bool>).set(false)
        default:
          break
        }
      })
      addressProperty.on(change: { [unowned self] (update) in
        if update === RemoteAddress.empty {
          (self.isAuthenticatedProperty as! PrimitiveProperty<Bool>).set(false)
        }
      })
    }

    func connect (to remote: RemoteAddress) -> Result<AuthenticationStatus, Error> {
      let result = Sm02.connect(to: remote)
      if case .success(AuthenticationStatus.success) = result {
        (addressProperty as! ObjectProperty<RemoteAddress>).set(remote)
      }
      return result
    }

    func disconnect (temporary: Bool = false) {
      Sm02.disconnect()
      if temporary == false {
        forget()
      }
    }

    func forget () {
      (addressProperty as! ObjectProperty<RemoteAddress>).set(RemoteAddress.empty)
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

    func resetPriority () {
      Person.priorityType = .none
      let outbound = Outbound.setPriority(person: .none)
      Sm02.send(message: outbound)
    }

    final class Person {

      fileprivate static var priorityType: PersonType = .none
      fileprivate let type: PersonType

      let nameProperty: ObserversManager<String> = FirableObserversManager<String>()
      let cardProperty: ObserversManager<StatusCard> = FirableObserversManager<StatusCard>()
      let passiveCardProperty: ObserversManager<StatusCard> = FirableObserversManager<StatusCard>()
      let scoreProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)

      var name: String = "" {
        willSet {
          let outbound = Outbound.setName(person: type, name: newValue)
          Sm02.send(message: outbound)
        }
        didSet {
          (nameProperty as! FirableObserversManager<String>).fire(with: name)
        }
      }
      var card: StatusCard = .none {
        willSet {
          let outbound = Outbound.setCard(person: type, status: newValue)
          Sm02.send(message: outbound)
        }
        didSet {
          (cardProperty as! FirableObserversManager<StatusCard>).fire(with: card)
        }
      }
      var passiveCard: StatusCard = .none {
        willSet {
          let outbound = Outbound.setCard(person: type, status: newValue)
          Sm02.send(message: outbound)
        }
        didSet {
          (cardProperty as! FirableObserversManager<StatusCard>).fire(with: card)
        }
      }
      var score: UInt8 {
        set {
          let outbound = Outbound.setScore(person: type, score: newValue)
          Sm02.send(message: outbound)
          (scoreProperty as! PrimitiveProperty<UInt8>).set(newValue)
        }
        get {
          (scoreProperty as! PrimitiveProperty<UInt8>).get()
        }
      }
      var isPriority: Bool { Person.priorityType == type }

      fileprivate init (type: PersonType) {
        self.type = type
      }

      func setPriority () {
        let outbound = Outbound.setPriority(person: type)
        Sm02.send(message: outbound)
        Person.priorityType = type
      }
    }
  }

  final class CompetitionManagement {

    let flags = FlagsManagement()

    let nameProperty: ObserversManager<String> = FirableObserversManager<String>()
    let weaponProperty: ObserversManager<Weapon> = FirableObserversManager<Weapon>()
    let periodProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)
    let periodTimeProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(0)
    let fightStatusProperty: ObserversManager<Decision> = FirableObserversManager<Decision>()

    var name: String = "" {
      willSet {
        let outbound = Outbound.setCompetition(name: newValue)
        Sm02.send(message: outbound)
      }
      didSet {
        (nameProperty as! FirableObserversManager<String>).fire(with: name)
      }
    }
    var weapon: Weapon = .none {
      willSet {
        let outbound = Outbound.setWeapon(weapon: newValue)
        Sm02.send(message: outbound)
      }
      didSet {
        (weaponProperty as! FirableObserversManager<Weapon>).fire(with: weapon)
      }
    }
    var period: UInt8 {
      set {
        let outbound = Outbound.setPeriod(period: newValue)
        Sm02.send(message: outbound)
        (periodProperty as! PrimitiveProperty<UInt8>).set(newValue)
      }
      get {
        (periodProperty as! PrimitiveProperty<UInt8>).get()
      }
    }
    var periodTime: UInt32 {
      set {
        let outbound = Outbound.setDefaultTime(time: newValue)
        Sm02.send(message: outbound)
        (periodTimeProperty as! PrimitiveProperty<UInt32>).set(newValue)
      }
      get {
        (periodTimeProperty as! PrimitiveProperty<UInt32>).get()
      }
    }
    var fightStatus: Decision = .continueFight {
      didSet {
        (fightStatusProperty as! FirableObserversManager<Decision>).fire(with: fightStatus)
      }
    }

    fileprivate init () {
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .broadcast(weapon, _, _, _, _) = inbound else {
          return
        }
        self.weapon = weapon
      })
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .fightResult(result) = inbound else {
          return
        }
        self.fightStatus = result
      })
    }

    func reset () {
      let outbound = Outbound.reset
      Sm02.send(message: outbound)
    }

    func swap () {
      let outbound = Outbound.swap
      Sm02.send(message: outbound)
    }

    final class FlagsManagement {

      let leftProperty: ObserversManager<FlagState> = FirableObserversManager<FlagState>()
      let rightProperty: ObserversManager<FlagState> = FirableObserversManager<FlagState>()

      private(set) var left: FlagState = .none {
        didSet {
          (leftProperty as! FirableObserversManager<FlagState>).fire(with: left)
        }
      }
      private(set) var right: FlagState = .none {
        didSet {
          (rightProperty as! FirableObserversManager<FlagState>).fire(with: left)
        }
      }

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

    let timeProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(0)
    let modeProperty: ObserversManager<TimerMode> = FirableObserversManager<TimerMode>()
    let stateProperty: ObserversManager<TimerState> = FirableObserversManager<TimerState>()
    let passive = PassiveManagement()

    var time: UInt32 {
      return (timeProperty as! PrimitiveProperty<UInt32>).get()
    }
    private(set) var mode: TimerMode = .pause {
      didSet {
        (modeProperty as! FirableObserversManager<TimerMode>).fire(with: mode)
      }
    }
    private(set) var state: TimerState = .suspended {
      didSet {
        (stateProperty as! FirableObserversManager<TimerState>).fire(with: state)
      }
    }

    fileprivate init () {
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .broadcast(_, _, _, timer, timerState) = inbound else {
          return
        }
        print("\(timer), \(timerState)")
        if self.time != timer {
          (self.timeProperty as! PrimitiveProperty<UInt32>).set(timer)
        }
        if self.state != timerState {
          self.state = timerState
        }
      })
    }

    func set (time: TimeAmount, mode: TimerMode) {
      let milliseconds = UInt32(time.nanoseconds / 1_000_000)

      let outbound = Outbound.setTimer(time: milliseconds, mode: mode)
      Sm02.send(message: outbound)

      (timeProperty as! PrimitiveProperty<UInt32>).set(milliseconds)
      self.mode = mode
    }

    func start (_ time: TimeAmount, mode: TimerMode) {
      set(time: time, mode: mode)
      start()
    }

    func start () {
      let outbound = Outbound.startTimer(state: .running)
      Sm02.send(message: outbound)
      state = .running
    }

    func stop () {
      let outbound = Outbound.startTimer(state: .suspended)
      Sm02.send(message: outbound)
      state = .suspended
    }

    final class PassiveManagement {

      var isVisibleProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      var isBlockedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      var defaultMillisecondsProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(60_000)
      var isMaxTimerReachedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      var isPauseFinishedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)

      var isVisible: Bool {
        set {
          let outbound = Outbound.passiveTimer(shown: newValue, locked: isBlocked, defaultMilliseconds: defaultMilliseconds)
          Sm02.send(message: outbound)
          (isVisibleProperty as! PrimitiveProperty<Bool>).set(newValue)
        }
        get {
          (isVisibleProperty as! PrimitiveProperty<Bool>).get()
        }
      }
      var isBlocked: Bool {
        set {
          let outbound = Outbound.passiveTimer(shown: isVisible, locked: newValue, defaultMilliseconds: defaultMilliseconds)
          Sm02.send(message: outbound)
          (isVisibleProperty as! PrimitiveProperty<Bool>).set(newValue)
        }
        get {
          (isVisibleProperty as! PrimitiveProperty<Bool>).get()
        }
      }
      var defaultMilliseconds: UInt32 {
        set {
          let outbound = Outbound.passiveTimer(shown: isVisible, locked: isBlocked, defaultMilliseconds: newValue)
          Sm02.send(message: outbound)
          (defaultMillisecondsProperty as! PrimitiveProperty<UInt32>).set(newValue)
        }
        get {
          (defaultMillisecondsProperty as! PrimitiveProperty<UInt32>).get()
        }
      }
      var isMaxTimerReached: Bool { (isMaxTimerReachedProperty as! PrimitiveProperty<Bool>).get() }
      var isPauseFinished: Bool { (isPauseFinishedProperty as! PrimitiveProperty<Bool>).get() }

      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          switch inbound {
          case .passiveMax:
            (self.isMaxTimerReachedProperty as! PrimitiveProperty<Bool>).set(true)
          case .pauseFinished:
            (self.isPauseFinishedProperty as! PrimitiveProperty<Bool>).set(true)
          default:
            return
          }
        })
      }
    }
  }

  final class VideoManagement {

    let replay = VideoReplayManagement()
    let player = VideoPlayerManagement()

    let recordModeProperty: ObserversManager<RecordMode> = FirableObserversManager<RecordMode>()

    var recordMode: RecordMode = .stop {
      willSet {
        let outbound = Outbound.record(recordMode: newValue)
        Sm02.send(message: outbound)
      }
      didSet {
        (recordModeProperty as! FirableObserversManager<RecordMode>).fire(with: recordMode)
      }
    }
    var routes: [Camera] = [] {
      willSet {
        let outbound = Outbound.videoRoutes(cameras: newValue)
        Sm02.send(message: outbound)
      }
    }

    fileprivate init () {
      // noop
    }

    func upload (to fileName: String) {
      let outbound = Outbound.loadFile(name: fileName)
      Sm02.send(message: outbound)
    }

    final class VideoPlayerManagement {

      let speedProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)
      let modeProperty: ObserversManager<RecordMode> = FirableObserversManager<RecordMode>()
      let timestampProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(0)

      var speed: UInt8 {
        set {
          let outbound = Outbound.player(speed: newValue, recordMode: mode, timestamp: 0)
          Sm02.send(message: outbound)
          (speedProperty as! PrimitiveProperty<UInt8>).set(newValue)
        }
        get {
          (speedProperty as! PrimitiveProperty<UInt8>).get()
        }
      }
      private(set) var mode: RecordMode = .stop {
        willSet {
          let outbound = Outbound.player(speed: speed, recordMode: newValue, timestamp: 0)
          Sm02.send(message: outbound)
        }
        didSet {
          (modeProperty as! FirableObserversManager<RecordMode>).fire(with: mode)
        }
      }
      var timestamp: UInt32 { (timestampProperty as! PrimitiveProperty<UInt32>).get() }

      func goto (_ timestamp: UInt32) {
        let outbound = Outbound.player(speed: speed, recordMode: mode, timestamp: timestamp)
        Sm02.send(message: outbound)
        (timestampProperty as! PrimitiveProperty<UInt32>).set(timestamp)
      }

      func stop () {
        mode = .stop
      }

      func play () {
        mode = .play
      }

      func pause () {
        mode = .pause
      }
    }

    final class VideoReplayManagement {

      let leftCounterProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)
      let rightCounterProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)
      let isReadyProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      let isReceivedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)

      var leftCounter: UInt8 {
        set {
          let outbound = Outbound.videoCounters(left: newValue, right: rightCounter)
          Sm02.send(message: outbound)
          (leftCounterProperty as! PrimitiveProperty<UInt8>).set(newValue)
        }
        get {
          (leftCounterProperty as! PrimitiveProperty<UInt8>).get()
        }
      }
      var rightCounter: UInt8 {
        set {
          let outbound = Outbound.videoCounters(left: leftCounter, right: newValue)
          Sm02.send(message: outbound)
          (rightCounterProperty as! PrimitiveProperty<UInt8>).set(newValue)
        }
        get {
          (rightCounterProperty as! PrimitiveProperty<UInt8>).get()
        }
      }
      var isReady: Bool { (isReadyProperty as! PrimitiveProperty<Bool>).get() }
      var isReceived: Bool { (isReceivedProperty as! PrimitiveProperty<Bool>).get() }

      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          guard case .videoReady(_) = inbound else {
            return
          }
          (self.isReadyProperty as! PrimitiveProperty<Bool>).set(true)
        })
        Sm02.on(message: { [unowned self] (inbound) in
          guard case .videoReceived = inbound else {
            return
          }
          (self.isReceivedProperty as! PrimitiveProperty<Bool>).set(true)
        })
      }
    }
  }

  final class DisplayManagement {

    let videoProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
    let photoProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
    let passiveProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
    let countryProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)

    var video: Bool {
      set {
        let outbound = Outbound.visibility(video: newValue, photo: photo, passive: passive, country: country)
        Sm02.send(message: outbound)
        (videoProperty as! PrimitiveProperty<Bool>).set(newValue)
      }
      get {
        (videoProperty as! PrimitiveProperty<Bool>).get()
      }
    }
    var photo: Bool {
      set {
        let outbound = Outbound.visibility(video: video, photo: newValue, passive: passive, country: country)
        Sm02.send(message: outbound)
        (photoProperty as! PrimitiveProperty<Bool>).set(newValue)
      }
      get {
        (photoProperty as! PrimitiveProperty<Bool>).get()
      }
    }
    var passive: Bool {
      set {
        let outbound = Outbound.visibility(video: video, photo: photo, passive: newValue, country: country)
        Sm02.send(message: outbound)
        (passiveProperty as! PrimitiveProperty<Bool>).set(newValue)
      }
      get {
        (passiveProperty as! PrimitiveProperty<Bool>).get()
      }
    }
    var country: Bool {
      set {
        let outbound = Outbound.visibility(video: video, photo: photo, passive: passive, country: newValue)
        Sm02.send(message: outbound)
        (countryProperty as! PrimitiveProperty<Bool>).set(newValue)
      }
      get {
        (countryProperty as! PrimitiveProperty<Bool>).get()
      }
    }
  }
}
