//
//  CountdownViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class CountdownViewController: UIViewController {

    var animated = true;
    
    @IBAction func tap(_ sender: UITapGestureRecognizer) {
        dismiss(animated: self.animated, completion: nil)
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated);
        self.animated = animated;
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
