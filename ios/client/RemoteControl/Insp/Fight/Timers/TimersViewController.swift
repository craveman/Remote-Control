//
//  TimersViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import SwiftUI

class TimersViewController: UIViewController {

     @IBOutlet weak var subView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    

    @IBSegueAction func showTimers(_ coder: NSCoder) -> UIViewController? {
        let rootView = PointsSwiftUIView(showModal: false);
        return UIHostingController(coder: coder, rootView: rootView)
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
