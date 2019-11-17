//
//  RemoteService.swift
//  RemoteControl
//
//  Created by Artem Labazin on 14.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import Sm02Client

class RemoteService {

  static let shared = RemoteService()
  
  private let innerRemoteServer: Atomic<RemoteServer?>

  var remoteServer: RemoteServer? {
    set {
      innerRemoteServer.store(newValue)
    }
    get {
      return innerRemoteServer.load()
    }
  }
  var visibility = Visibility()
  var videoCounters = VideoCounters()
  var persons = [PersonType: PersonStatus]()
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
    innerRemoteServer = Atomic<RemoteServer?>(nil)
  }

  func setName (for person: PersonType, _ name: String) {
    let outbound = Outbound.setName(person: person, name: name)
    Sm02.send(message: outbound)
    if var personStatus = persons[person] {
      personStatus.name = name
    } else {
      let personStatus = PersonStatus(name: name)
      persons[person] = personStatus
    }
  }

  func setScore (for person: PersonType, _ score: UInt8) {
    let outbound = Outbound.setScore(person: person, score: score)
    Sm02.send(message: outbound)
    if var personStatus = persons[person] {
      personStatus.score = score
    } else {
      let personStatus = PersonStatus(score: score)
      persons[person] = personStatus
    }
  }

  func setCard (for person: PersonType, _ card: StatusCard) {
    let outbound = Outbound.setCard(person: person, status: card)
    Sm02.send(message: outbound)
    if var personStatus = persons[person] {
      personStatus.card = card
    } else {
      let personStatus = PersonStatus(card: card)
      persons[person] = personStatus
    }
  }

  func setPriority (for person: PersonType) {
    let outbound = Outbound.setPriority(person: person)
    Sm02.send(message: outbound)
    if var personStatus = persons[person] {
      personStatus.priority = true
    } else {
      let personStatus = PersonStatus(priority: true)
      persons[person] = personStatus
    }
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

  func connect (to remote: RemoteServer) {
    Sm02.connect(to: remote)
  }

  func disconnect () {
    Sm02.disconnect()
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
