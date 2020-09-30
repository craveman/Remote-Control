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

public enum FightPhase: Int, Decodable {
  case standby = 1
  case active = 2
  case ended = 3
  case none = 0
}

class InspSettings: ObservableObject {
  @Published var isConnected: Bool = rs.connection.isAuthenticated && rs.connection.isConnected
  @Published var isEthernetMode: Bool = rs.competition.cyranoWorks
  @Published var fightPhase: FightPhase = .none
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
  @Published var selectedReplay: (_: String, title: String)? = nil
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
      } else if canPlay {
        rs.video.player.pause()
      }
      print("active \(isPlayActive)")
    }
  }
 
  @Published var selectedSpeed: Double = 0 {
    didSet {
      if(selectedSpeed.rounded() != oldValue.rounded()) {
        Vibration.selection()
      }
      let val = UInt8(selectedSpeed.rounded())
      if val != rs.video.player.speed {
        rs.video.player.speed = val
      }
      print("speed \(selectedSpeed)")
    }
  }
  @Published var currentPosition: Double = 0 {
    didSet {
      let val = UInt32(currentPosition.rounded())
      print("goto \(currentPosition)")
      if (rs.video.player.timestamp != val) {
        rs.video.player.goto(val)
      }
    }
  }
  @Published var replayLength: UInt32 = 0
  
  func refreshVideoList() -> Void {
    var list = rs.video.replay.recordsList
    list.sort(by: FileNameConverter.sortByTimeAsc)
    self.replaysList.removeAll()
    self.replaysList.append(contentsOf: list)
  }
  
  func loaded() -> Bool {
    print("loaded")
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
    canPlay = false
    isPlayActive = false
    selectedReplay = nil
    rs.video.player.stop()
  }
  
  func choose(filename: String?) -> Void {
    selectedReplay = nil
    guard filename != nil else {
      canPlay = false
      print("choosen filename is nil")
      return
    }
    let name = filename!
    guard replaysList.contains(name) else {
      print("choosen filename '\(name)' is not presented in list")
      Vibration.notification(.error)
      return
    }
    selectedReplay = (name, FileNameConverter.getTitle(name))
    rs.video.upload(to: filename!)
  }
}

class FightSettings: ObservableObject {
  var PAUSE_DISSMISED_DEFERED_ACTION_TIMER: Timer? = nil
  var ETHERNET_DEFERED_ACTION_TIMER: Timer? = nil
  var PAUSE_FINISHED_CANCELABLE: AnyCancellable? = nil
  var savedTime: TimeAmount? = nil
  private var isSyncing = false
  @Published var ethernetFightPhase: FightPhase = .none
  @Published var ethernetNextFightTitle: String = ""
  @Published var ethernetActionIsLocked: Bool = false
  @Published var leftCardP: StatusCard = .passiveNone
  @Published var rightCardP: StatusCard = .passiveNone
  @Published var leftCard: StatusCard = .none
  @Published var rightCard: StatusCard = .none
  @Published var leftScore: UInt8 = 0 {
    didSet {
      guard !isSyncing else {
        return
      }
      print("leftScore didSet \(leftScore)")
      rs.persons.left.score = leftScore
    }
  }
  @Published var rightScore: UInt8 = 0 {
    didSet {
      guard !isSyncing else {
        return
      }
      print("rightScore didSet \(rightScore)")
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
  
  @Published private(set) var period: Int = 1
  
  func syncPeriod() -> Void {
    period = Int(rs.competition.period)
  }
  
  func setPeriod(_ next: UInt8) -> Void {
    rs.competition.setPeriod(next)
    period = Int(next)
    rs.timer.set(time: INSPIRATION_DEFAULT_FIGHT_TIME, mode: .main)
  }
  
  func turnOnEthLockTimer() -> Void {
    if self.ethernetActionIsLocked {
      print("self.ethernetActionIsLocked already turned on")
      return;
    }
  
    self.ethernetActionIsLocked = true
    
    self.ETHERNET_DEFERED_ACTION_TIMER = withDelay({
      self.ethernetActionIsLocked = false
      self.ETHERNET_DEFERED_ACTION_TIMER = nil
    }, 5)
  }
  
  func turnOffEthLockTimer() -> Void {
    self.ethernetActionIsLocked = false
    
    if self.ETHERNET_DEFERED_ACTION_TIMER != nil {
      self.ETHERNET_DEFERED_ACTION_TIMER?.invalidate()
    }
  }
  
  func resetBout() {
    self.resetPassive()
    self.setPeriod(1)
    self.leftScore = 0
    self.rightScore = 0
    self.resetCards()
    self.resetVideo()
    self.resetPriority()
  }
  
  func syncFightState(_ state: FightState) {
    DispatchQueue.main.async {
      self.isSyncing = true
      self.syncPeriod()
      
      self.leftScore = rs.persons.left.score
      self.leftCard = rs.persons.left.card
      self.leftCardP = rs.persons.left.passiveCard
      
      self.rightScore = rs.persons.right.score
      self.rightCard = rs.persons.right.card
      self.rightCardP = rs.persons.right.passiveCard
      
      self.ethernetFightPhase = state.ethernetStatus == .waiting ? .none : .active
      self.ethernetNextFightTitle = state.ethernetStatus == .waiting ? getFightName(left: state.matchLeftFighterData.matchName, right: state.matchRightFighterData.matchName): ""
      self.turnOffEthLockTimer()
      withDelay({
        self.isSyncing = false
      }, RemoteService.SYNC_INTERVAL)
     
      print("loadFightConfig sync complete")
      
    }
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
