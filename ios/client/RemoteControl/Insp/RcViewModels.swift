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

public enum InspirationRCTypes {
  case Basic
  case Video
}

class InspSettings: ObservableObject {
  @Published var isConnected: Bool = rs.connection.isAuthenticated && rs.connection.isConnected
  
  @Published var tab: Int = !rs.connection.isConnected ? 2 : 1
  @Published var fightSwitchActiveTab: Int = rs.timer.mode == .main ? 0 : 1
  
  @Published var shouldShowTimerView: Bool = rs.timer.mode == .main && rs.timer.state == .running
  @Published var shouldShowPauseView: Bool = rs.timer.mode == .pause && rs.timer.state == .running
  @Published var shouldShowMedicalView: Bool = rs.timer.mode == .medicine && rs.timer.state == .running
  @Published var shouldShowVideoRCView: Bool = false
  @Published var shouldShowVideoSelectView: Bool = false
  @Published var videoModalSelectedTab: Int = 0
  private var vc: UIViewController?
  
  public func setVC (vc: UIViewController) {
    self.vc = vc
  }
  
  
  func switchRCType (_ type: InspirationRCTypes) {
    switch type {
    case .Video:
      self.shouldShowVideoRCView = true
      
    case .Basic:
      self.shouldShowVideoRCView = false
    }
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
  @Published var isEnabled = false
  @Published var selectedReplay: String? = nil
  @Published var replaysList: [String] = []
  @Published var isRecordActive: Bool = false {
    didSet {
      if (isRecordActive) {
        rs.video.recordMode = .play
        UIApplication.shared.isIdleTimerDisabled = true
      } else {
        rs.video.recordMode = .pause
        UIApplication.shared.isIdleTimerDisabled = false
      }
      print("Record mode mow is \(isRecordActive)")
    }
  }
  @Published var canPlay: Bool = false
  @Published var isPlayActive: Bool = false {
    didSet {
      if (isPlayActive) {
        rs.video.player.play()
      } else {
        rs.video.player.pause()
      }
      print("active \(isPlayActive)")
    }
  }
 
  @Published var selectedSpeed: Double = 0 {
    didSet {
      if(selectedSpeed.rounded() != oldValue.rounded()) {
        Vibration.impact()
      }
      rs.video.player.speed = UInt8(selectedSpeed.rounded())
      print("speed \(selectedSpeed)")
    }
  }
  @Published var currentPosition: Double = 0 {
    didSet {
      print("goto \(currentPosition)")
      rs.video.player.goto(UInt32(currentPosition.rounded()))
    }
  }
  @Published var replayLength: UInt32 = 0
  
  func refreshVideoList() -> Void {
    self.replaysList.removeAll()
    var list = rs.video.replay.recordsList
    list.sort(by: FileNameConverter.sortByTimeAsc)
    self.replaysList.append(contentsOf: rs.video.replay.recordsList)
//    self.replaysList = rs.video.replay.recordsList
  }
  
  func loaded() -> Bool {
    print("loaded \(selectedReplay ?? "nil")")
    guard selectedReplay != nil else {
      // ejected before loaded
      return false
    }
    
    canPlay = true
    rs.video.player.standBy()
    selectedSpeed = Double(rs.video.player.speed)
    currentPosition = 0
    return true
  }
  
  func eject() -> Void {
    rs.video.player.stop()
    choose(name: nil)
    canPlay = false
  }
  
  func choose(name: String?) -> Void {
    selectedReplay = name
    guard name != nil else {
      canPlay = false
      print("choosen filename is nil")
      return
    }
    guard replaysList.contains(name!) else {
      print("choosen filename '\(name!)' is not presented in list")
      return
    }
    
    rs.video.upload(to: name!)
  }
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
  
  @Published var period: Int = 0
  
  func setPeriod(_ next: Int) -> Void {
    
    rs.competition.period = UInt8(next + 1)
    period = next
    rs.timer.set(time: INSPIRATION_DEFAULT_FIGHT_TIME, mode: .main)
    
    print("settings.period updated to \(rs.competition.period) with Int \(next)")
  }
  
  func resetBout() {
    self.resetPassive()
    self.setPeriod(0)
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
    rs.video.replay.clear()
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
