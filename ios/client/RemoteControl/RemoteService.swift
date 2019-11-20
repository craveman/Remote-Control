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

  var visibility = Visibility()
  var videoCounters = VideoCounters()
  var period: UInt8 = 0 {
    willSet {
      Sm02.send(message: .setPeriod(period: newValue))
    }
  }
  var weapon: Weapon = .none {
    willSet {
      Sm02.send(message: .setWeapon(weapon: newValue))
    }
  }
  var defaultTime: UInt32 = 0 {
    willSet {
      Sm02.send(message: .setDefaultTime(time: newValue))
    }
  }
  var competitionName: String = "" {
    willSet {
      Sm02.send(message: .setCompetition(name: newValue))
    }
  }
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

  func setTimer (time: UInt32, mode: TimerMode) {
    let outbound = Outbound.setTimer(time: time, mode: mode)
    Sm02.send(message: outbound)
  }

  func startTimer (state: TimerState) {
    let outbound = Outbound.startTimer(state: state)
    Sm02.send(message: outbound)
  }

  func swap () {
    let outbound = Outbound.swap
    Sm02.send(message: outbound)
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

  func reset () {
    let outbound = Outbound.reset
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

    var addressProperty = ObjectProperty<RemoteAddress>(RemoteAddress.empty)
    var isConnectedProperty = PrimitiveProperty<Bool>(false)
    var isAuthenticatedProperty = PrimitiveProperty<Bool>(false)

    var isConnected: Bool { isConnectedProperty.get() }
    var isAuthenticated: Bool { isAuthenticatedProperty.get() }
    var address: RemoteAddress? {
      let value = addressProperty.get()
      return Optional.some(value)
          .flatMap { $0.isEmpty() ? nil : $0 }
    }

    init () {
      Sm02.on(message: { [unowned self] (inbound) in
        if case .authentication(.success) = inbound {
          self.isAuthenticatedProperty.set(true)
        }
      })
      Sm02.on(event: { [unowned self] (event) in
        switch event {
        case .connected:
          self.isConnectedProperty.set(true)
        case .disconnected:
          self.isConnectedProperty.set(false)
          self.isAuthenticatedProperty.set(false)
        default:
          break
        }
      })
    }

    func connect (to remote: RemoteAddress) -> Result<Void, Error> {
      let result = Sm02.connect(to: remote)
      if case .success(_) = result {
        addressProperty.set(remote)
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

      var name: String = "" {
        willSet {
          let outbound = Outbound.setName(person: type, name: newValue)
          Sm02.send(message: outbound)
        }
      }
      var score: UInt8 = 0 {
        willSet {
          let outbound = Outbound.setScore(person: type, score: newValue)
          Sm02.send(message: outbound)
        }
      }
      var card: StatusCard = .none {
        willSet {
          let outbound = Outbound.setCard(person: type, status: newValue)
          Sm02.send(message: outbound)
        }
      }
      var priority: Bool {
        return Person.priorityType == type
      }

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

  struct PersonStatus {

    var name: String = ""
    var score: UInt8 = 0
    var card: StatusCard = .none
    var priority: Bool = false
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
