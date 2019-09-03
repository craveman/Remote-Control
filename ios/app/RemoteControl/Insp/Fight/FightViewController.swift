//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class FightViewController: UIViewController {

    @IBOutlet weak var fightSubView: UIView!
    
    @IBOutlet weak var viewSelector: UISegmentedControl!
    
    let pointsCtrl = PointsViewController()
    let timersCtrl = TimersTableViewController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Selected \(viewSelector?.selectedSegmentIndex ?? 0)");
        if let segmentedControl = viewSelector as UISegmentedControl? {
            segmentedControl.addTarget(self, action: Selector(("indexChanged:")), for: .valueChanged)
        }
        
        fightSubView.addSubview(pointsCtrl.view)
        
        // Do any additional setup after loading the view.
    }
    
    @objc func indexChanged(_ sender: UISegmentedControl) {
        switch viewSelector?.selectedSegmentIndex {
            case 0:
                print("Select 0")
                fightSubView.addSubview(pointsCtrl.view)
            case 1:
                print("Select 1")
                fightSubView.addSubview(timersCtrl.view)
            default:
                print("Select: None")
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
