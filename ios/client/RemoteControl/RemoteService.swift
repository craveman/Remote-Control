//
//  RemoteService.swift
//  RemoteControl
//
//  Created by Artem Labazin on 14.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import Sm02Client
import NIOConcurrencyHelpers

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
          (self.isAuthenticatedProperty as! PrimitiveProperty<Bool>).set(true)
        case .disconnected:
          (self.isAuthenticatedProperty as! PrimitiveProperty<Bool>).set(false)
          (self.isAuthenticatedProperty as! PrimitiveProperty<Bool>).set(false)
        default:
          break
        }
      })
    }

    func connect (to remote: RemoteAddress) -> Result<Void, Error> {
      let result = Sm02.connect(to: remote)
      if case .success(_) = result {
        (addressProperty as! ObjectProperty<RemoteAddress>).set(remote)
      }
      return result
    }

    func disconnect () {
      Sm02.disconnect()
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
      let outbound = Outbound.setPriority(person: .none)
      Sm02.send(message: outbound)
    }

    final class Person {

      fileprivate static var priorityType: PersonType = .none
      fileprivate let type: PersonType

      let nameProperty: ObserversManager<String> = FirableObserversManager<String>()
      let cardProperty: ObserversManager<StatusCard> = FirableObserversManager<StatusCard>()
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

    let nameProperty: ObserversManager<String> = FirableObserversManager<String>()
    let weaponProperty: ObserversManager<Weapon> = FirableObserversManager<Weapon>()
    let periodProperty: ObservableProperty<UInt8> = PrimitiveProperty<UInt8>(0)
    let periodTimeProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(0)

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

    fileprivate init () {
      // noop
    }

    func reset () {
      let outbound = Outbound.reset
      Sm02.send(message: outbound)
    }

    func swap () {
      let outbound = Outbound.swap
      Sm02.send(message: outbound)
    }
  }

  final class TimerManagement {

    let timeProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(0)
    let modeProperty: ObserversManager<TimerMode> = FirableObserversManager<TimerMode>()
    let stateProperty: ObserversManager<TimerState> = FirableObserversManager<TimerState>()

    var time: UInt32 {
      set {
        let outbound = Outbound.setTimer(time: newValue, mode: mode)
        Sm02.send(message: outbound)
        (timeProperty as! PrimitiveProperty<UInt32>).set(newValue)
      }
      get {
        (timeProperty as! PrimitiveProperty<UInt32>).get()
      }
    }
    var mode: TimerMode = .pause {
      willSet {
        let outbound = Outbound.setTimer(time: time, mode: newValue)
        Sm02.send(message: outbound)
      }
      didSet {
        (modeProperty as! FirableObserversManager<TimerMode>).fire(with: mode)
      }
    }
    private(set) var state: TimerState = .suspended

    fileprivate init () {
      Sm02.on(message: { [unowned self] (inbound) in
        guard case let .broadcast(_, _, _, timer, timerState) = inbound else {
          return
        }
        if self.time != timer {
          (self.timeProperty as! PrimitiveProperty<UInt32>).set(timer)
        }
        if self.state != timerState {
          (self.stateProperty as! FirableObserversManager<TimerState>).fire(with: timerState)
        }
      })
    }

    func start () {
      state = .running
      let outbound = Outbound.startTimer(state: state)
      Sm02.send(message: outbound)
      (stateProperty as! FirableObserversManager<TimerState>).fire(with: state)
    }

    func stop () {
      state = .suspended
      let outbound = Outbound.startTimer(state: state)
      Sm02.send(message: outbound)
      (stateProperty as! FirableObserversManager<TimerState>).fire(with: state)
    }

    final class PassiveManagement {

      var isVisibleProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      var isBlockedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
      var defaultMillisecondsProperty: ObservableProperty<UInt32> = PrimitiveProperty<UInt32>(60_000)

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

      fileprivate init () {
        // noop
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
