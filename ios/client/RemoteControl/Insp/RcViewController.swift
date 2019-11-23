//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import SwiftUI
import Sm02Client

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
    DispatchQueue.main.asyncAfter(deadline: .now()) {
      //            print("\(delay) milliseconds later")
      callback()
    }
  }
  
  private func setSubscriptions() {
    rs.timer.timeProperty.on(change: { update in
      print(update)
      print(getTimeString(update))
      self.onMainThread({self.game.time = update})
    })
    rs.timer.stateProperty.on(change: { timerState in
      self.onMainThread({self.game.isRunning = timerState == .running})
    })
    
    
    //        left
    rs.persons.left.scoreProperty.on(change: { score in
      self.onMainThread({self.game.leftScore = score})
    })
    rs.persons.left.cardProperty.on(change: { card in
      self.onMainThread({self.setCard(card, .left)})
    })
    
    //        right
    rs.persons.right.scoreProperty.on(change: { score in
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
    self.game.time = 180000
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
  @Published var leftScore: UInt8 = 0
  @Published var rightScore: UInt8 = 0
  @Published var time: UInt32 = 500000
  @Published var isRunning = false
}
