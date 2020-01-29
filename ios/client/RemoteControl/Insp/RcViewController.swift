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
  
  //  todo: use saved subscribtions tokens
  internal var subUuids: [UUID] = [];
  
  lazy var fightSwiftUIHostVC: UIViewController = {
    
    var view = RcSwiftUIView()
    
    self.fight = view
    
    var vc = UIHostingController(rootView: view.environmentObject(game).environmentObject(rcModel).environmentObject(playbackController))
    self.addViewControllerAsChildViewController(childViewController: vc)
    return vc
  }()
  override func viewDidLoad() {
    self.overrideUserInterfaceStyle = .light
    super.viewDidLoad()
//    setSubscriptions()
//    updateView()
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    if (isBeingPresented) {
      updateView()
    }
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    setSubscriptions()
  }
  
  override func viewWillDisappear(_ animated: Bool) {
      super.viewWillDisappear(animated)
      if isBeingDismissed {
        clearSubscriptions()
      }
  }
  
  override func viewDidDisappear(_ animated: Bool) {
      super.viewDidDisappear(animated)
      fightSwiftUIHostVC.view.isHidden = true
  }
  
  private func updateView() {
    fightSwiftUIHostVC.view.isHidden = false
  }
  
  private func onMainThread(_ callback:  @escaping () -> Void) {
    DispatchQueue.main.async {
      callback()
    }
  }
  
  private func clearSubscriptions() {
    print("clearSubscriptions", subUuids.count)
    subUuids.forEach({ uuid in
      rs.remove(event: uuid)
    })
  }
  
  private func setSubscriptions() {
    print("setSubscriptions", subUuids.count)
    let auth$ = rs.connection.isAuthenticatedProperty.on(change: { isAuth in
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
    
    subUuids.append(auth$)
    
    let connected$ = rs.connection.isConnectedProperty.on(change: { isConnected in
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
    
    subUuids.append(connected$)
    
    let time$ = rs.timer.timeProperty.on(change: { update in
      guard self.game.time != update else {
        return
      }
      self.onMainThread({self.game.time = update})
    })
    
    subUuids.append(time$)
    var count = 0;
    let state$ = rs.timer.stateProperty.on(change: { timerState in
      self.onMainThread({
      let isRun = rs.timer.state == .running
      let mode = rs.timer.mode
        count += 1;
        let savedCount = count;
        print(savedCount, "change", rs.timer.state)
        print("state ", self.rcModel.shouldShowTimerView)
        print("prepareView mode:", mode )
        self.rcModel.prepareView(mode)
        withDelay({
          print("withDelay state ", self.rcModel.shouldShowTimerView)
          self.onMainThread({
            print("on Main thread showTimer:", mode == .main && isRun, "mode:", mode,"isRun:", isRun)
            self.rcModel.shouldShowTimerView = mode == .main && isRun
            self.rcModel.shouldShowPauseView = mode == .pause && isRun
            self.rcModel.shouldShowMedicalView = mode == .medicine && isRun
          })
        }, 0.21)
      })
    })
    
    subUuids.append(state$)
    
    let passive$ = rs.display.passiveProperty.on(change: { showPassive in
      guard self.game.showPassive != showPassive else {
        return
      }
      self.onMainThread({self.game.showPassive = showPassive})
    })
    
    subUuids.append(passive$)
    
    let weapon$ = rs.competition.weaponProperty.on(change: { weapon in
      guard self.game.weapon != weapon else {
        return
      }
      self.onMainThread({
        self.game.weapon = weapon
      })
    })
    
    subUuids.append(weapon$)
    
    let timerMax$ = rs.timer.passive.isMaxTimerReachedProperty.on(change: { reached in
      if (reached) {
        Vibration.on()
      }
    })
    
    subUuids.append(timerMax$)
    
    //        left
    let lScore$ = rs.persons.left.scoreProperty.on(change: { score in
      guard self.game.leftScore != score else {
        return
      }
      self.onMainThread({self.game.leftScore = score})
    })
    let lCard$ = rs.persons.left.cardProperty.on(change: { card in
      self.onMainThread({self.game.setCard(card, .left)})
    })
    
    subUuids.append(lScore$)
    subUuids.append(lCard$)
    
    //        right
    let rScore$ = rs.persons.right.scoreProperty.on(change: { score in
      guard self.game.rightScore != score else {
        return
      }
      self.onMainThread({self.game.rightScore = score})
    })
    let rCard$ = rs.persons.right.cardProperty.on(change: { card in
      self.onMainThread({self.game.setCard(card, .right)})
    })
    
    subUuids.append(rScore$)
    subUuids.append(rCard$)
  }
  
  private func addViewControllerAsChildViewController(childViewController: UIViewController) {
    addChild(childViewController)
    
    fightSubView.addSubview(childViewController.view)
    childViewController.view.frame = fightSubView.bounds
    childViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    childViewController.didMove(toParent: self)
  }
  
}
