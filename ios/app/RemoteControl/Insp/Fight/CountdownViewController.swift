//
//  CountdownViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import networking

class CountdownViewController: UIViewController {

    private var animated = true;
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.animated = animated;
        self.view.isUserInteractionEnabled = true
        let tapGesture = UITapGestureRecognizer(target: self, action: Selector(("handleTap:")))
        self.view.addGestureRecognizer(tapGesture)
    }
    
    @objc func handleTap(_ sender : UIView?) {
        NetworkManager.shared.send(message: Outbound.startTimer(state: .suspended))
        dismiss(animated: self.animated, completion: nil)
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
