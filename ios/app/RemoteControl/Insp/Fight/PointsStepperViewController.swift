//
//  PointsStepperViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class PointsStepperViewController: UIViewController {
    private var handler : ((_ diff: Int) -> Void)?
    public var canDecrease = true {
        didSet {
            self.minus?.isEnabled = self.canDecrease;
        }
    }
    public var canIncrease = true {
        didSet {
            self.plus?.isEnabled = self.canIncrease;
        }
    }

    @IBOutlet weak var minus: UIButton! {
        didSet {
            self.minus?.layer.borderWidth = 1
            self.minus?.layer.borderColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
            self.minus?.layer.cornerRadius = UIGlobals.buttonCornerRadius
        }
    }
    
    @IBOutlet weak var plus: UIButton! {
        didSet {
            self.plus?.layer.cornerRadius = UIGlobals.buttonCornerRadius
        }
    }
    
    @IBAction func decreasePoints(_ sender: UIButton) {
        if handler != nil {
            handler!(-1)
        }
        lockControl()
    }
    
    @IBAction func increasePoints(_ sender: UIButton) {
        if handler != nil {
            handler!(1)
        }
        lockControl()
    }
    
    public func setHandler(_ callback: @escaping (_ diff: Int) -> Void) {
        self.handler = callback
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    private func lockControl() {
        [minus, plus].forEach { btn in
            Utils.delay({
                UITools.disableButtonForTime(btn)
            }, ms: 1)
        }
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
