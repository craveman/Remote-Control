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

class FightViewController: UIViewController {

    @IBOutlet weak var fightSubView: UIView!
    internal var fight: FightSectionSwiftUIView?
    internal var game = FightSettings()
    lazy var fightSwiftUIHost: UIViewController = {
        
        var view = FightSectionSwiftUIView()
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
        Sm02.on(message: { [unowned self] (inbound) in
          guard case let .broadcast(weapon, left, right, timer, timerState) = inbound else {
            return
          }
          print("weapon=\(weapon),left=\(left),right=\(right),timer=\(timer),timerState=\(timerState)")
            self.game.isRunning = timerState == .running
            self.game.time = timer
        })
        updateView()
        // Do any additional setup after loading the view.
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

class FightSettings: ObservableObject {
    @Published var leftScore = 0
    @Published var rightScore = 0
    @Published var time: UInt32 = 500000
    @Published var isRunning = false
}
