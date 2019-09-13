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
    
    lazy var pointsCtrl: PointsViewController = {
        let stbrd = UIStoryboard(name: "FightStoryboard", bundle: nil)

        var vc = stbrd.instantiateViewController(withIdentifier: "FightPoints") as! PointsViewController

        self.addViewControllerAsChildViewController(childViewController: vc)
        print(vc)
        return vc
    }()
    
    
    lazy var timersCtrl: TimersTableViewController = {
        let stbrd = UIStoryboard(name: "FightStoryboard", bundle: nil)

        var vc = stbrd.instantiateViewController(withIdentifier: "FightTimers") as! TimersTableViewController
        
        vc.fightNavigationBack = { [weak self] in
            self?.viewSelector.selectedSegmentIndex = 0
            self?.updateView()
        }

        self.addViewControllerAsChildViewController(childViewController: vc)
        print(vc)
        return vc
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("Selected \(viewSelector?.selectedSegmentIndex ?? 0)");
        if let segmentedControl = viewSelector as UISegmentedControl? {
            segmentedControl.addTarget(self, action: Selector(("indexChanged:")), for: .valueChanged)
        }
        
        updateView()
      
        // Do any additional setup after loading the view.
    }
    
    @objc func indexChanged(_ sender: UISegmentedControl) {
        switch viewSelector?.selectedSegmentIndex {
            case 0:
                print("Select 0")

            case 1:
                print("Select 1")

            default:
                print("Select: None")
        }
        
        updateView()
    }
    
    private func updateView() {
        print("update view")
        
        pointsCtrl.view.isHidden = !(viewSelector.selectedSegmentIndex == 0)
        timersCtrl.view.isHidden = (viewSelector.selectedSegmentIndex == 0)
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
