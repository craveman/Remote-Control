//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import SwiftUI
import struct NIO.TimeAmount

let INSPIRATION_DEF_TIMOUT = TimeAmount.minutes(3)
let GAME_DEFAULT_TIME: UInt32 = UInt32(INSPIRATION_DEF_TIMOUT.nanoseconds/1_000_000)

class RcViewController: UIViewController {

  @IBOutlet weak var fightSubView: UIView!
  internal var fight: RcSwiftUIView?
  internal var game = FightSettings()
  lazy var fightSwiftUIHost: UIViewController = {

    var view = RcSwiftUIView()
    self.fight = view
    self.updateViewState()
    var vc = UIHostingController(rootView: view.environmentObject(game))
    self.addViewControllerAsChildViewController(childViewController: vc)

    return vc
  }()

  func updateViewState() -> Void {

  }

  override func viewDidLoad() {
    super.viewDidLoad()
    setSubscriptions()
    updateView()
    // Do any additional setup after loading the view.
  }

  private func onMainThread(_ callback:  @escaping () -> Void) {
    DispatchQueue.main.async {
      //            print("\(delay) milliseconds later")
      callback()
    }
  }

  private func setSubscriptions() {
    rs.connection.isAuthenticatedProperty.on(change: { isAuth in
      guard isAuth == false else {
        return
      }
      guard let presenter = self.presentingViewController as? ConnectionsViewController else {
        return
      }
      presenter.dismiss(animated: true, completion: {
        presenter.start()
      })
    })
    
    rs.timer.timeProperty.on(change: { update in
      guard self.game.time != update else {
        return
      }
      self.onMainThread({self.game.time = update})
    })
    rs.timer.stateProperty.on(change: { timerState in
      let isRun = timerState == .running;
      guard self.game.isRunning != isRun else {
        return
      }
      self.onMainThread({self.game.isRunning = isRun})
    })

    rs.display.passiveProperty.on(change: { showPassive in
      guard self.game.showPassive != showPassive else {
        return
      }
      self.onMainThread({self.game.showPassive = showPassive})
    })
    
    rs.competition.weaponProperty.on(change: { weapon in
      guard self.game.weapon != weapon else {
        return
      }
      self.onMainThread({self.game.weapon = weapon})
    })


    //        left
    rs.persons.left.scoreProperty.on(change: { score in
      guard self.game.leftScore != score else {
        return
      }
      self.onMainThread({self.game.leftScore = score})
    })
    rs.persons.left.cardProperty.on(change: { card in
      self.onMainThread({self.setCard(card, .left)})
    })

    //        right
    rs.persons.right.scoreProperty.on(change: { score in
      guard self.game.rightScore != score else {
        return
      }
      self.onMainThread({self.game.rightScore = score})
    })
    rs.persons.right.cardProperty.on(change: { card in
      self.onMainThread({self.setCard(card, .right)})
    })
  }

  private func setCard(_ card: StatusCard, _ pType: PersonType) {
    switch card {
    case .none, .yellow, .red, .black:
      switch pType {
      case .left:
        self.game.leftCard = card
      case .right:
        self.game.rightCard = card
      default:
        return
      }
    case .passiveNone, .passiveYellow, .passiveRed, .passiveBlack:
      switch pType {
      case .left:
        self.game.leftCardP = card
      case .right:
        self.game.rightCardP = card
      default:
        return
      }
    }

  }

  private func updateView() {
    print("update view")

    fightSwiftUIHost.view.isHidden = false
    updateGame()
  }

  private func updateGame() {
    self.game.isRunning = false
    self.game.time = GAME_DEFAULT_TIME
  }

  private func addViewControllerAsChildViewController(childViewController: UIViewController) {

    addChild(childViewController)

    fightSubView.addSubview(childViewController.view)
    childViewController.view.frame = fightSubView.bounds
    childViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]

    childViewController.didMove(toParent: self)

  }

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
  @Published var tab: Int = 1
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
