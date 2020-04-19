//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin, Sergei Andreev. All rights reserved.
//

import UIKit
import SwiftUI
import class Combine.AnyCancellable

class RcViewController: UIViewController {
  
  @IBOutlet weak var fightSubView: UIView!
  internal var fight: RcSwiftUIView?
  internal var game = FightSettings()
  internal var rcModel = InspSettings()
  internal var playbackController = PlaybackControls()
  
  //  todo: use saved subscribtions tokens
  internal var subscriptions: [AnyCancellable] = []
  
  lazy var fightSwiftUIHostVC: UIViewController = {
    
    var view = RcSwiftUIView()
    
    self.fight = view
    
    self.rcModel.setVC(vc: self.presentationController!.presentedViewController)
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
    print("clearSubscriptions", subscriptions.count)
    subscriptions.forEach({ subscription in
      subscription.cancel()
    })
  }
  
  private func setSubscriptions() {
    print("setSubscriptions", subscriptions.count)
    
    let auth$ = rs.connection.$isAuthenticated.on(change: { isAuth in
      //      guard self.rcModel.isConnected == true else {
      //        return
      //      }
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
        presenter.start(true)
      })
    })
    
    subscriptions.append(auth$)
    
    let connected$ = rs.connection.$isConnected.on(change: { isConnected in
      self.onMainThread({
        self.playbackController.eject()
        self.rcModel.switchRCType(.Basic)
      })
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
    
    subscriptions.append(connected$)
    
    let time$ = rs.timer.$time.on(change: { update in
      guard self.game.time != update else {
        return
      }
      self.onMainThread({self.game.time = update})
    })
    
    subscriptions.append(time$)
    
    var timerWait: Timer? = nil
    let state$ = rs.timer.$state.on(change: { timerState in
      self.onMainThread({
        self.rcModel.prepareView(rs.timer.mode)
      })
      if timerWait != nil {
        timerWait!.invalidate()
      }
      timerWait = withDelay({
        timerWait = nil
        print("withDelay  shouldShowTimerView state: ", self.rcModel.shouldShowTimerView)
        self.onMainThread({
          let mode = rs.timer.mode
          let isRun = rs.timer.state == .running
          print("on Main thread showTimer:", mode == .main && isRun, "mode:", mode,"isRun:", isRun)
          self.rcModel.shouldShowTimerView = mode == .main && isRun
          self.rcModel.shouldShowPauseView = mode == .pause && isRun
          self.rcModel.shouldShowMedicalView = mode == .medicine && isRun
        })
      }, RemoteService.SYNC_INTERVAL)
    })
    
    subscriptions.append(state$)
    
    let passive$ = rs.display.$passive.on(change: { showPassive in
      guard self.game.showPassive != showPassive else {
        return
      }
      self.onMainThread({self.game.showPassive = showPassive})
    })
    
    subscriptions.append(passive$)
    
    let weapon$ = rs.competition.$weapon.on(change: { weapon in
      guard self.game.weapon != weapon else {
        return
      }
      self.onMainThread({
        self.game.weapon = weapon
      })
    })
    
    subscriptions.append(weapon$)
    
    let timerMax$ = rs.timer.passive.$isMaxTimerReached.on(change: { reached in
      if (reached) {
        Vibration.on()
      }
    })
    
    subscriptions.append(timerMax$)
    
    //        left
    let lScore$ = rs.persons.left.$score.on(change: { score in
      guard self.game.leftScore != score else {
        return
      }
      self.onMainThread({self.game.leftScore = score})
    })
    let lCard$ = rs.persons.left.$card.on(change: { card in
      self.onMainThread({self.game.setCard(card, .left)})
    })
    
    subscriptions.append(lScore$)
    subscriptions.append(lCard$)
    
    //        right
    let rScore$ = rs.persons.right.$score.on(change: { score in
      guard self.game.rightScore != score else {
        return
      }
      self.onMainThread({self.game.rightScore = score})
    })
    let rCard$ = rs.persons.right.$card.on(change: { card in
      self.onMainThread({self.game.setCard(card, .right)})
    })
    
    subscriptions.append(rScore$)
    subscriptions.append(rCard$)
    
    let videoRecordReady$ = rs.video.replay.$isReady.on(change: { v in
      self.onMainThread({self.playbackController.refreshVideoList()})
    })
    
    
    let videoRecordLoaded$ = rs.video.replay.$isReceived.on(change: { _ in
      self.onMainThread({
        
        let ok = self.playbackController.loaded()
        
        if ok {
          self.rcModel.shouldShowVideoSelectView = false
          
          withDelay({
            self.rcModel.switchRCType(.Video)
          }, 0.125)
          
        }
      })
    })
    
    subscriptions.append(videoRecordReady$)
    subscriptions.append(videoRecordLoaded$)
  }
  
  private func addViewControllerAsChildViewController(childViewController: UIViewController) {
    addChild(childViewController)
    
    fightSubView.addSubview(childViewController.view)
    childViewController.view.frame = fightSubView.bounds
    childViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    childViewController.didMove(toParent: self)
  }
  
}
