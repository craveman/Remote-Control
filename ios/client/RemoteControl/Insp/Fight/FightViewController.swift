//
//  FightViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import SwiftUI

class FightViewController: UIViewController {

    @IBOutlet weak var fightSubView: UIView!
    
    
    lazy var fightSwiftUIHost: UIViewController = {

        var vc = UIHostingController(rootView: FightSectionSwiftUIView())
        self.addViewControllerAsChildViewController(childViewController: vc)
       
        return vc
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateView()
        // Do any additional setup after loading the view.
    }
    
    private func updateView() {
        print("update view")
        
        fightSwiftUIHost.view.isHidden = false;
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
