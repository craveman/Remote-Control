//
//  RcViewModels.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 09.12.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import UIKit.UIViewController
import struct NIO.TimeAmount
import class Combine.AnyCancellable

let INSPIRATION_DEFAULT_FIGHT_TIME = TimeAmount.minutes(3)
let GAME_DEFAULT_TIME: UInt32 = UInt32(INSPIRATION_DEFAULT_FIGHT_TIME.nanoseconds/1_000_000)
let INSPIRATION_MED_TIMOUT = TimeAmount.minutes(5)
let INSPIRATION_SHORT_TIMOUT = TimeAmount.minutes(1)
let INSPIRATION_MAX_PERIOD: Int = 99

class InspSettings: ObservableObject {
  @Published var isConnected: Bool = rs.connection.isAuthenticated && rs.connection.isConnected
  
  @Published var tab: Int = !rs.connection.isConnected ? 2 : 1
  @Published var fightSwitchActiveTab: Int = rs.timer.mode == .main ? 0 : 1
  
  @Published var shouldShowTimerView: Bool = rs.timer.mode == .main && rs.timer.state == .running
  @Published var shouldShowPauseView: Bool = rs.timer.mode == .pause && rs.timer.state == .running
  @Published var shouldShowMedicalView: Bool = rs.timer.mode == .medicine && rs.timer.state == .running
  private var vc: UIViewController?
  
  public func setVC (vc: UIViewController) {
    self.vc = vc
  }
  
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
  
  func onReset() {
    prepareView(.main)
  }
  
  public func quit() {
    vc?.presentingViewController?.dismiss(animated: true, completion: {
      print("InspSettings::quit quit complete")
    })
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
      guard rs.timer.passive.defaultMilliseconds != passiveDefaultTimeMs else {
        return
      }
      rs.timer.passive.defaultMilliseconds = passiveDefaultTimeMs
    }
  }
  @Published var showPassive = rs.timer.passive.isVisible
  
  @Published var weapon: Weapon = .none
  
  @Published var period: Int = 0 {
    didSet {
      print("settings.period updated to \(period)")
      rs.competition.period = UInt8(period + 1)
      rs.timer.set(time: INSPIRATION_DEFAULT_FIGHT_TIME, mode: .main)
    }
  }
  
  func resetBout() {
    self.resetPassive()
    self.period = 0
    self.leftScore = 0
    self.rightScore = 0
    self.resetCards()
    self.resetVideo()
    self.resetPriority()
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
  
  func resetVideo() -> Void {
    rs.video.replay.leftCounter = 2
    rs.video.replay.rightCounter = 2
  }
  
  func resetPriority() -> Void {
    rs.persons.resetPriority()
  }
  
  func resetPassive() -> Void {
    rs.timer.passive.isVisible = false // todo: select state from fight defaults (mode, weapon etc.)
    // tricky thing:
    // as we do not have opotunity to fetch DEFAULT passive we will force user to rewrite default passive timer value on first
    // passive timer setting update (isVisible / isBlocked) because it is send in one command with default
    rs.timer.passive.defaultMilliseconds = RemoteService.TimerManagement.PassiveManagement.DEFAULT_PASSIVE_TIMER
    self.passiveDefaultTimeMs = rs.timer.passive.defaultMilliseconds
  }
}
