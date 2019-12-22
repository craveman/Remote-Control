//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin, Sergei Andreev. All rights reserved.
//

import UIKit
import SwiftUI

class RcViewController: UIViewController {
  
  @IBOutlet weak var fightSubView: UIView!
  internal var fight: RcSwiftUIView?
  internal var game = FightSettings()
  internal var rcModel = InspSettings()
  internal var playbackController = PlaybackControls()
  
//  todo: save subscribe tokens
  internal var subUuids: [UUID] = []
  
  lazy var fightSwiftUIHostVC: UIViewController = {
    
    var view = RcSwiftUIView()
      
    self.fight = view
    
    var vc = UIHostingController(rootView: view.environmentObject(game).environmentObject(rcModel).environmentObject(playbackController))
    self.addViewControllerAsChildViewController(childViewController: vc)
    return vc
  }()
  override func viewDidLoad() {
    super.viewDidLoad()
    setSubscriptions()
    updateView()
  }
  
  private func updateView() {
    fightSwiftUIHostVC.view.isHidden = false
  }
  
  private func onMainThread(_ callback:  @escaping () -> Void) {
    DispatchQueue.main.async {
      callback()
    }
  }
  
  private func setSubscriptions() {
    rs.connection.isAuthenticatedProperty.on(change: { isAuth in
      guard isAuth == false else {
        if !self.rcModel.isConnected {
          self.onMainThread({
            self.rcModel.isConnected = isAuth && rs.connection.isConnected
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
        if !self.rcModel.isConnected {
          self.onMainThread({
            self.rcModel.isConnected = rs.connection.isAuthenticated && isConnected
            self.rcModel.tab = 1
          })
        }
        return
      }
      self.onMainThread({
        self.rcModel.isConnected = rs.connection.isAuthenticated && isConnected
        self.rcModel.tab = 2
        
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
        self.rcModel.shouldShowTimerView = rs.timer.mode == .main && isRun
        self.rcModel.shouldShowPauseView = rs.timer.mode == .pause && isRun
        self.rcModel.shouldShowMedicalView = rs.timer.mode == .medicine && isRun
        
        self.game.isRunning = isRun
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
    
    rs.timer.passive.isMaxTimerReachedProperty.on(change: { reached in
      if (reached) {
        Vibration.on()
      }
    })
    
    //        left
    rs.persons.left.scoreProperty.on(change: { score in
      guard self.game.leftScore != score else {
        return
      }
      self.onMainThread({self.game.leftScore = score})
    })
    rs.persons.left.cardProperty.on(change: { card in
      self.onMainThread({self.game.setCard(card, .left)})
    })
    
    //        right
    rs.persons.right.scoreProperty.on(change: { score in
      guard self.game.rightScore != score else {
        return
      }
      self.onMainThread({self.game.rightScore = score})
    })
    rs.persons.right.cardProperty.on(change: { card in
      self.onMainThread({self.game.setCard(card, .right)})
    })
  }
  
  private func addViewControllerAsChildViewController(childViewController: UIViewController) {
    addChild(childViewController)
    
    fightSubView.addSubview(childViewController.view)
    childViewController.view.frame = fightSubView.bounds
    childViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    childViewController.didMove(toParent: self)
  }
  
}
