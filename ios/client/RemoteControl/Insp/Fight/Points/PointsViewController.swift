//
//  PointsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
<<<<<<< Updated upstream

let passiveTimerDefaultValue = UInt32(60)
let passiveTimerIsEnabled = true

class PointsViewController: UIViewController {

    let rs = RemoteService.shared

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
        rs.setScore(for: .left, n)
    }
    @IBOutlet weak var passiveToggleButton: UIButton! {
        didSet {
            self.passiveToggleButton?.layer.cornerRadius = UIGlobals.buttonCornerRadius
        }
    }

    private func setScore(right n: UInt8) {
        self.score = (self.score.left, n)
        rs.setScore(for: .right, n)
    }
=======
import SwiftUI

class PointsViewController: UIViewController {
>>>>>>> Stashed changes

    @IBOutlet weak var subView: UIView!
    
    override func viewDidLoad() {
       super.viewDidLoad()

<<<<<<< Updated upstream
        passiveToggleButton?.addTarget(self, action: Selector(("togglePassiveTimerAction:")), for: .touchUpInside)
        rs.passiveTimer(shown: passiveTimerIsEnabled, locked: passiveLocked, defaultMilliseconds: passiveTimerDefaultValue)
        updateView()
        // Do any additional setup after loading the view.
    }

    @objc func startTimerAction(_ sender: UIButton) {
        rs.startTimer(state: .running)
    }
    @objc func togglePassiveTimerAction(_ sender: UIButton) {
        passiveLocked = !passiveLocked;
        rs.passiveTimer(shown: passiveTimerIsEnabled, locked: passiveLocked, defaultMilliseconds: passiveTimerDefaultValue)
        updateView()
=======
       // Do any additional setup after loading the view.
   }
   

    @IBSegueAction func showPoints(_ coder: NSCoder)-> UIViewController? {
        let rootView = PointsSwiftUIView(showModal: false);
        return UIHostingController(coder: coder, rootView: rootView)
>>>>>>> Stashed changes
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
