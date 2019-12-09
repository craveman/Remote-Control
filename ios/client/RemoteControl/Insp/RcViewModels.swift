//
//  RcViewModels.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 09.12.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation

class InspSettings: ObservableObject {
  @Published var isConnected: Bool = rs.connection.isAuthenticated && rs.connection.isConnected
  @Published var shouldShowTimerView: Bool = rs.timer.mode == .main && rs.timer.state == .running
  @Published var shouldShowPauseView: Bool = rs.timer.mode == .pause && rs.timer.state == .running
  @Published var shouldShowMedicalView: Bool = rs.timer.mode == .medicine && rs.timer.state == .running
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
  @Published var isRunning = false
  @Published var showPassive = true
  @Published var holdPassive = false
  @Published var weapon: Weapon = .none
  @Published var tab: Int = !rs.connection.isConnected ? 2 : 1
  @Published var presentedModal: UUID?
  @Published var period: Int = 0 {
    didSet {
      print("settings.period updated to \(period)")
      rs.competition.period = UInt8(period + 1)
      rs.timer.set(time: INSPIRATION_DEF_TIMOUT, mode: .main)
    }
  }
  @Published var fightSwitchActiveTab: Int = 0
  
  func resetBout() {
    self.leftScore = 0
    self.rightScore = 0
    self.time = GAME_DEFAULT_TIME
    self.tab = 1
    self.fightSwitchActiveTab = 0
    self.isRunning = false
    self.leftCard = .none
    self.rightCard = .none
    self.leftCardP = .passiveNone
    self.rightCardP = .passiveNone
  }
}
