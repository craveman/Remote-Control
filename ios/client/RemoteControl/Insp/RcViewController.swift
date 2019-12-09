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
  internal var remoteModel = InspSettings()
  internal var playbackController = PlaybackControls()
  lazy var fightSwiftUIHost: UIViewController = {
    
    var view = RcSwiftUIView()
    self.fight = view
    self.updateViewState()
    var vc = UIHostingController(rootView: view.environmentObject(game).environmentObject(remoteModel)
      .environmentObject(playbackController)
    )
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
        if !self.remoteModel.isConnected {
          self.onMainThread({
            self.remoteModel.isConnected = isAuth && rs.connection.isConnected
          })          
        }
        return
      }
      guard let presenter = self.presentingViewController as? ConnectionsViewController else {
        return
      }
      presenter.dismiss(animated: true, completion: {
        presenter.start()
      })
    })
    
    rs.connection.isConnectedProperty.on(change: { isConnected in
      guard isConnected == false else {
        if !self.remoteModel.isConnected {
          self.onMainThread({
            self.remoteModel.isConnected = rs.connection.isAuthenticated && isConnected
            self.game.tab = 1
          })
        }
        return
      }
      self.onMainThread({
        self.remoteModel.isConnected = rs.connection.isAuthenticated && isConnected
        self.game.tab = 2
        
      })
    })
    
    rs.timer.timeProperty.on(change: { update in
      guard self.game.time != update else {
        return
      }
      self.onMainThread({self.game.time = update})
    })
    rs.timer.stateProperty.on(change: { timerState in
      let isRun = timerState == .running
      guard self.game.isRunning != isRun else {
        return
      }
      self.onMainThread({
        self.game.isRunning = isRun
        self.remoteModel.shouldShowTimerView = isRun
      })
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
