//
//  RcViewModels.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 09.12.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import struct NIO.TimeAmount
import class Combine.AnyCancellable

let INSPIRATION_DEF_TIMOUT = TimeAmount.minutes(3)
let GAME_DEFAULT_TIME: UInt32 = UInt32(INSPIRATION_DEF_TIMOUT.nanoseconds/1_000_000)
let INSPIRATION_MED_TIMOUT = TimeAmount.minutes(5)
let INSPIRATION_SHORT_TIMOUT = TimeAmount.minutes(1)
let INSPIRATION_MAX_PERIOD: Int = 9

class InspSettings: ObservableObject {
  @Published var isConnected: Bool = rs.connection.isAuthenticated && rs.connection.isConnected
  
  @Published var tab: Int = !rs.connection.isConnected ? 2 : 1
  @Published var fightSwitchActiveTab: Int = rs.timer.mode == .main ? 0 : 1
  
  @Published var shouldShowTimerView: Bool = rs.timer.mode == .main && rs.timer.state == .running
  @Published var shouldShowPauseView: Bool = rs.timer.mode == .pause && rs.timer.state == .running
  @Published var shouldShowMedicalView: Bool = rs.timer.mode == .medicine && rs.timer.state == .running
  
  func prepareView(_ mode: TimerMode) {
    DispatchQueue.main.async { [unowned self] in
      switch mode {
      case .main:
        self.tab = 1
        self.fightSwitchActiveTab = 0
      case .pause:
        self.tab = 1
        self.fightSwitchActiveTab = 1
      case .medicine:
        self.tab = 1
        self.fightSwitchActiveTab = 1
      }
    }
  }
  
  func reset() {
    prepareView(.main)
  }
}

class PlaybackControls: ObservableObject {
  @Published var selectedPlayer: PersonType = .left {
    didSet {
      self.isActive = false
      self.selectedReplay = 0
      self.currentPosition = 0
      self.selectedSpeed = 10
    }
  }
  @Published var selectedReplay: UInt8 = 0
  @Published var isActive: Bool = false {
    didSet {
      if (isActive) {
        rs.video.player.play()
      } else {
        rs.video.player.pause()
      }
    }
  }
  @Published var selectedSpeed: UInt8 = 10
  @Published var currentPosition: UInt8 = 0
  @Published var replayLength: UInt32 = 0
}

class FightSettings: ObservableObject {
  var PAUSE_DISSMISED_DEFERED_ACTION_TIMER: Timer? = nil
  var PAUSE_FINISHED_CANCELABLE: AnyCancellable? = nil
  var savedTime: TimeAmount? = nil
  @Published var leftCardP: StatusCard = .passiveNone
  @Published var rightCardP: StatusCard = .passiveNone
  @Published var leftCard: StatusCard = .none
  @Published var rightCard: StatusCard = .none
  @Published var leftScore: UInt8 = 0 {
    didSet {
      rs.persons.left.score = leftScore
    }
  }
  @Published var rightScore: UInt8 = 0 {
    didSet {
      rs.persons.right.score = rightScore
    }
  }
  @Published var time: UInt32 = GAME_DEFAULT_TIME
  @Published var passiveDefaultTimeMs: UInt32 = rs.timer.passive.defaultMilliseconds {
    didSet {
      rs.timer.passive.defaultMilliseconds = passiveDefaultTimeMs
    }
  }
  @Published var showPassive = rs.timer.passive.isVisible
  @Published var holdPassive = rs.timer.passive.isBlocked
  @Published var weapon: Weapon = .none
  
  @Published var period: Int = 0 {
    didSet {
      print("settings.period updated to \(period)")
      rs.competition.period = UInt8(period + 1)
      rs.timer.set(time: INSPIRATION_DEF_TIMOUT, mode: .main)
    }
  }
  
  func resetBout() {
    self.period = 0
    self.leftScore = 0
    self.rightScore = 0
    self.resetCards()
  }
  
  
  func setCard(_ card: StatusCard, _ pType: PersonType) {
    switch card {
    case .none, .yellow, .red, .black:
      switch pType {
      case .left:
        self.leftCard = card
      case .right:
        self.rightCard = card
      default:
        return
      }
    case .passiveNone, .passiveYellow, .passiveRed, .passiveBlack:
      switch pType {
      case .left:
        self.leftCardP = card
      case .right:
        self.rightCardP = card
      default:
        return
      }
    }
    
  }
  
  func resetCards() {
    self.leftCard = .none
    self.rightCard = .none
    self.leftCardP = .passiveNone
    self.rightCardP = .passiveNone
  }
}
