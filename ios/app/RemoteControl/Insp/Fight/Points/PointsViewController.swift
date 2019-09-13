//
//  PointsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import networking

let passiveTimerDefaultValue = UInt32(60)
let passiveTimerIsEnabled = true

class PointsViewController: UIViewController {
    private var score: (left: UInt8, right: UInt8) = (0, 0) {
        didSet {
            self.updateView()
        }
    }
    
    private var passiveLocked = false;
    
    
    @IBOutlet weak var startButton: UIButton! {
        didSet {
            self.startButton?.layer.cornerRadius = UIGlobals.buttonCornerRadius
        }
    }
    @IBOutlet weak var left: UIView!
    @IBOutlet weak var right: UIView!
    private func setScore(left n: UInt8) {
        self.score = (n, self.score.right)
        send(Outbound.setScore(person: .left, score: n))
    }
    @IBOutlet weak var passiveToggleButton: UIButton! {
        didSet {
            self.passiveToggleButton?.layer.cornerRadius = UIGlobals.buttonCornerRadius
        }
    }
    
    private func setScore(right n: UInt8) {
        self.score = (self.score.left, n)
        send(Outbound.setScore(person: .right, score: n))
    }
    
    private func send(_ out: Outbound) {
        NetworkManager.shared.send(message: out)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        startButton?.addTarget(self, action: Selector(("startTimerAction:")), for: .touchUpInside)
        
        passiveToggleButton?.addTarget(self, action: Selector(("togglePassiveTimerAction:")), for: .touchUpInside)
        send(Outbound.passiveTimer(shown: passiveTimerIsEnabled, locked: passiveLocked, defaultMilliseconds: passiveTimerDefaultValue))
        updateView()
        // Do any additional setup after loading the view.
    }
    
    @objc func startTimerAction(_ sender: UIButton) {
        send(Outbound.startTimer(state: .running))
    }
    @objc func togglePassiveTimerAction(_ sender: UIButton) {
        passiveLocked = !passiveLocked;
        send(Outbound.passiveTimer(shown: passiveTimerIsEnabled, locked: passiveLocked, defaultMilliseconds: passiveTimerDefaultValue))
        updateView()
    }
    
    private func updateView() {
        getPointsVC(left)?.canDecrease = self.score.left > 0;
        getPointsVC(left)?.setHandler({ diff in
            if self.score.left == UInt8(0) && diff < 0 {
                return
            }
            self.setScore(left: UInt8(Int(self.score.left) + diff))
            
        })
        
        getPointsVC(right)?.canDecrease = self.score.right > 0;
        getPointsVC(right)?.setHandler({ diff in
            if self.score.right == UInt8(0) && diff < 0 {
                return
            }
            self.setScore(right: UInt8(Int(self.score.right) + diff))
        })
        
        passiveToggleButton?.isHidden = !passiveTimerIsEnabled
        passiveToggleButton?.isSelected = passiveTimerIsEnabled
        passiveToggleButton?.layer.borderWidth = UIGlobals.activeButtonBorder.width
        passiveToggleButton?.layer.borderColor = passiveLocked ? UIGlobals.activeButtonBorder.color : passiveToggleButton?.layer.backgroundColor
        
    }
    
    private func getPointsVC(_ view: UIView?) -> PointsStepperViewController? {
        print (view?.subviews.first?.next as? PointsStepperViewController ?? "none")
        return view?.subviews.first?.next as? PointsStepperViewController
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
