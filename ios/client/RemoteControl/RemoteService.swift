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

  var visibility = Visibility()
  var videoCounters = VideoCounters()
  var videoRoutes: [Camera] = [] {
    willSet {
      Sm02.send(message: .videoRoutes(cameras: newValue))
    }
  }
  var loadFileName: String = "" {
    willSet {
      Sm02.send(message: .loadFile(name: newValue))
    }
  }
  var recordMode: RecordMode = .stop {
    willSet {
      Sm02.send(message: .record(recordMode: newValue))
    }
  }

  private init() {
    // noop
  }

  func passiveTimer (shown: Bool, locked: Bool, defaultMilliseconds: UInt32) {
    let outbound = Outbound.passiveTimer(shown: shown, locked: locked, defaultMilliseconds: defaultMilliseconds)
    Sm02.send(message: outbound)
  }

  func player (speed: UInt8, recordMode: RecordMode, timestamp: UInt32) {
    let outbound = Outbound.player(speed: speed, recordMode: recordMode, timestamp: timestamp)
    Sm02.send(message: outbound)
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

    var addressProperty: ObservableProperty<RemoteAddress> = ObjectProperty<RemoteAddress>(RemoteAddress.empty)
    var isConnectedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)
    var isAuthenticatedProperty: ObservableProperty<Bool> = PrimitiveProperty<Bool>(false)

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
  }

  struct Visibility {

    var video: Bool = false {
      willSet {
        let outbound = Outbound.visibility(video: newValue, photo: photo, passive: passive, country: country)
        Sm02.send(message: outbound)
      }
    }
    var photo: Bool = false {
      willSet {
        let outbound = Outbound.visibility(video: video, photo: newValue, passive: passive, country: country)
        Sm02.send(message: outbound)
      }
    }
    var passive: Bool = false {
      willSet {
        let outbound = Outbound.visibility(video: video, photo: photo, passive: newValue, country: country)
        Sm02.send(message: outbound)
      }
    }
    var country: Bool = false {
      willSet {
        let outbound = Outbound.visibility(video: video, photo: photo, passive: passive, country: newValue)
        Sm02.send(message: outbound)
      }
    }
  }

  struct VideoCounters {

    var left: UInt8 = 0 {
      willSet {
        let outbound = Outbound.videoCounters(left: newValue, right: right)
        Sm02.send(message: outbound)
      }
    }
    var right: UInt8 = 0 {
      willSet {
        let outbound = Outbound.videoCounters(left: left, right: newValue)
        Sm02.send(message: outbound)
      }
    }
  }
}
