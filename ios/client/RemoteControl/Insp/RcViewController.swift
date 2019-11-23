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
    
    private func setSubscriptions() {
        rs.timer.timeProperty.on(change: { update in
          self.game.time = update
        })
        rs.timer.stateProperty.on(change: { timerState in
          self.game.isRunning = timerState == .running
        })
        
        
//        left
        rs.persons.left.scoreProperty.on(change: { score in
          self.game.leftScore = score
        })
        
//        right
        rs.persons.right.scoreProperty.on(change: { score in
                 self.game.rightScore = score
        })
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
    @Published var leftScore: UInt8 = 0
    @Published var rightScore: UInt8 = 0
    @Published var time: UInt32 = 500000
    @Published var isRunning = false
}
