//
//  RemoteService.swift
//  RemoteControl
//
//  Created by Artem Labazin on 14.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import struct Foundation.UUID
import typealias Foundation.Published
import class Foundation.NSTimer.Timer
import class Dispatch.DispatchQueue

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

    @Published
    private(set) var isConnected: Bool = false

    @Published
    private(set) var isAuthenticated: Bool = false

    @Published
    private(set) var address: RemoteAddress? = nil

    init () {
      Sm02.on(message: { [unowned self] (inbound) in
        if case .authentication(.success) = inbound {
          self.isAuthenticated = true
        }
      })
      Sm02.on(event: { [unowned self] (event) in
        switch event {
        case .connected:
          self.isConnected = true
        case .disconnected:
          self.isConnected = false
        default:
          break
        }
      })

      $address.on(change: { [unowned self] (update) in
        if update === RemoteAddress.empty {
          self.isAuthenticated = false
        }
      })
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
      isConnected = false;
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

    func resetPriority () {
      Person.priorityType = .none
      let outbound = Outbound.setPriority(person: .none)
      Sm02.send(message: outbound)
    }

    final class Person {

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

      fileprivate init (type: PersonType) {
        self.type = type
        $name.on(change: { update in
          let outbound = Outbound.setName(person: type, name: update)
          Sm02.send(message: outbound)
        })
        $card.on(change: { update in
          let outbound = Outbound.setCard(person: type, status: update)
          Sm02.send(message: outbound)
        })
        $passiveCard.on(change: { update in
          let outbound = Outbound.setCard(person: type, status: update)
          Sm02.send(message: outbound)
        })
        $score.on(change: { update in
          let outbound = Outbound.setScore(person: type, score: update)
          Sm02.send(message: outbound)
        })
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

    @Published
    var name = ""

    @Published
    var weapon: Weapon = .none

    @Published
    var period: UInt8 = 0

    @Published
    var periodTime: UInt32 = 0

    @Published
    private(set) var fightStatus: Decision = .continueFight

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

      $name.on(change: { update in
        let outbound = Outbound.setCompetition(name: update)
        Sm02.send(message: outbound)
      })
      $weapon.on(change: { update in
        // Sm02 could sometimes reject '.none'
        guard self.weapon != update else {
          return;
        }
        let outbound = Outbound.setWeapon(weapon: update)
        Sm02.send(message: outbound)
      })
      $period.on(change: { update in
        let outbound = Outbound.setPeriod(period: update)
        Sm02.send(message: outbound)
      })
      $periodTime.on(change: { update in
        let outbound = Outbound.setDefaultTime(time: update)
        Sm02.send(message: outbound)
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
          print("pauseFinished: \(self.isPauseFinished)")
        case let .broadcast(_, _, _, timer, timerState):
          print("\(timer), \(timerState)")
          if self.time != timer {
            self.time = timer
          }
          if self.state != timerState {
            print("toggle state")
            self.state = timerState
            if self.state == .running && (self.mode == .medicine || self.mode == .pause) && self.isPauseFinished {
              self.isPauseFinished = false
              print("pauseFinished set: \(self.isPauseFinished)")
            }
          }
        default:
          break
        }
      })
    }

    func set (time: TimeAmount, mode: TimerMode) {
      let milliseconds = UInt32(time.nanoseconds / 1_000_000)

      let outbound = Outbound.setTimer(time: milliseconds, mode: mode)
      Sm02.send(message: outbound)

      self.time = milliseconds
      self.mode = mode
    }

    func start (_ time: TimeAmount, mode: TimerMode) {
      set(time: time, mode: mode)
      Timer.scheduledTimer(withTimeInterval: 0.21, repeats: false) {[weak self] _ in
        self?.start()
      }
    }

    func start () {
      let outbound = Outbound.startTimer(state: .running)
      Sm02.send(message: outbound)
      passive.unlock()
//      state = .running
    }

    func stop () {
      let outbound = Outbound.startTimer(state: .suspended)
      Sm02.send(message: outbound)
//      state = .suspended
    }

    final class PassiveManagement {

      @Published
      var isVisible = false

      @Published
      var isBlocked = false

      @Published
      var defaultMilliseconds: UInt32 = 0

      @Published
      private(set) var isMaxTimerReached = false

      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          switch inbound {
          case .passiveMax:
            self.isMaxTimerReached = true
            Timer.scheduledTimer(withTimeInterval: 0.5, repeats: false) {_ in
              self.isMaxTimerReached = false
            }
          default:
            return
          }
        })

        $isVisible.on(change: { [unowned self] (update) in
          let outbound = Outbound.passiveTimer(
            shown: update,
            locked: self.isBlocked,
            defaultMilliseconds: self.defaultMilliseconds
          )
          Sm02.send(message: outbound)
        })
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
        })
        $defaultMilliseconds.on(change: { [unowned self] (update) in
          let outbound = Outbound.passiveTimer(
            shown: self.isVisible,
            locked: self.isBlocked,
            defaultMilliseconds: update
          )
          Sm02.send(message: outbound)
        })
      }

      fileprivate func unlock() {
        isBlocked = false
      }
    }
  }

  final class VideoManagement {

    let replay = VideoReplayManagement()
    let player = VideoPlayerManagement()

    @Published
    var recordMode: RecordMode = .stop

    @Published
    var routes: [Camera] = []

    fileprivate init () {
      $recordMode.on(change: { [unowned self] (update) in
        let outbound = Outbound.record(recordMode: update)
        Sm02.send(message: outbound)
      })
      $routes.on(change: { update in
        let outbound = Outbound.videoRoutes(cameras: update)
        Sm02.send(message: outbound)
      })
    }

    func upload (to fileName: String) {
      let outbound = Outbound.loadFile(name: fileName)
      Sm02.send(message: outbound)
    }

    final class VideoPlayerManagement {

      @Published
      var speed: UInt8 = 0

      @Published
      private(set) var mode: RecordMode = .stop

      @Published
      private(set) var timestamp: UInt32 = 0

      fileprivate init () {
        $speed.on(change: { [unowned self] update in
          let outbound = Outbound.player(speed: update, recordMode: self.mode, timestamp: 0)
          Sm02.send(message: outbound)
        })
        $mode.on(change: { [unowned self] update in
          let outbound = Outbound.player(speed: self.speed, recordMode: update, timestamp: 0)
          Sm02.send(message: outbound)
        })
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
        mode = .pause
      }
    }

    final class VideoReplayManagement {

      @Published
      var leftCounter: UInt8 = 0

      @Published
      var rightCounter: UInt8 = 0

      @Published
      private(set) var isReady = false

      @Published
      private(set) var isReceived = false

      fileprivate init () {
        Sm02.on(message: { [unowned self] (inbound) in
          guard case .videoReady(_) = inbound else {
            return
          }
          self.isReady = true
        })
        Sm02.on(message: { [unowned self] (inbound) in
          guard case .videoReceived = inbound else {
            return
          }
          self.isReceived = true
        })

        $leftCounter.on(change: { [unowned self] (update) in
          let outbound = Outbound.videoCounters(left: update, right: self.rightCounter)
          Sm02.send(message: outbound)
        })
        $rightCounter.on(change: { [unowned self] (update) in
          let outbound = Outbound.videoCounters(left: self.leftCounter, right: update)
          Sm02.send(message: outbound)
        })
      }
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

    fileprivate init () {
      $video.on(change: { [unowned self] (update) in
        let outbound = Outbound.visibility(
          video: update,
          photo: self.photo,
          passive: self.passive,
          country: self.country
        )
        Sm02.send(message: outbound)
      })
      $photo.on(change: { [unowned self] (update) in
        let outbound = Outbound.visibility(
          video: self.video,
          photo: update,
          passive: self.passive,
          country: self.country
        )
        Sm02.send(message: outbound)
      })
      $passive.on(change: { [unowned self] (update) in
        let outbound = Outbound.visibility(
          video: self.video,
          photo: self.photo,
          passive: update,
          country: self.country
        )
        Sm02.send(message: outbound)
      })
      $country.on(change: { [unowned self] (update) in
        let outbound = Outbound.visibility(
          video: self.video,
          photo: self.photo,
          passive: self.passive,
          country: update
        )
        Sm02.send(message: outbound)
      })
    }
  }
}
