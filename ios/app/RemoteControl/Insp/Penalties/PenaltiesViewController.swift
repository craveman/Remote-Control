//
//  PenaltiesViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class PenaltiesViewController: UIViewController {
    
    let storyBoard = "PenaltiesStoryboard"
    let vcName = "PenaltiesCards"

    @IBOutlet weak var penaltiesSubView: UIView!
    
    @IBOutlet weak var viewSelector: UISegmentedControl!
    
    lazy var penaltiesCtrl: PenaltiesCardsViewController = {
        let stbrd = UIStoryboard(name: storyBoard, bundle: nil)
        
        var vc = stbrd.instantiateViewController(withIdentifier: vcName) as! PenaltiesCardsViewController
        self.addViewControllerAsChildViewController(childViewController: vc)
       
        return vc
    }()
    
    
    lazy var pCardsCtrl: PenaltiesCardsViewController = {
        let stbrd = UIStoryboard(name: storyBoard, bundle: nil)
        
        var vc = stbrd.instantiateViewController(withIdentifier: vcName) as! PenaltiesCardsViewController
        
        self.addViewControllerAsChildViewController(childViewController: vc)
        return vc
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
       
        if let segmentedControl = viewSelector as UISegmentedControl? {
            segmentedControl.addTarget(self, action: Selector(("indexChanged:")), for: .valueChanged)
        }
        
        updateView()
        
        // Do any additional setup after loading the view.
    }
    
    @objc func indexChanged(_ sender: UISegmentedControl) {
        updateView()
    }
    
    private func updateView() {
        penaltiesCtrl.view.isHidden = !(viewSelector.selectedSegmentIndex == 0)
        pCardsCtrl.view.isHidden = (viewSelector.selectedSegmentIndex == 0)
        
        penaltiesCtrl.setType(.basic)
        pCardsCtrl.setType(.passive)
    }
    
    private func addViewControllerAsChildViewController(childViewController: UIViewController) {
        
        addChild(childViewController)
        
        penaltiesSubView.addSubview(childViewController.view)
        childViewController.view.frame = penaltiesSubView.bounds
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
