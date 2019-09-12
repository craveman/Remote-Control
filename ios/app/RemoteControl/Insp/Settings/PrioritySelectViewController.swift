//
//  PrioritySelectViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 11/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import networking

class PrioritySelectViewController: UIViewController {
    
    var currentPerson: PersonType = .none

    @IBOutlet weak var setPriorityButton: UIButton!
    
    @IBOutlet weak var resetPriorityButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
 
        updateStyles()
        [setPriorityButton, resetPriorityButton].forEach({ btn in
            btn?.addTarget(self, action: Selector(("setPriorityAction:")), for: .touchUpInside)
        })
        
        updateView()
        // Do any additional setup after loading the view.
    }
    
    
    @objc func setPriorityAction(_ sender: UIButton) {
        switch sender {
        case setPriorityButton: setPriority(Bool.random() ? .left : .right)
        case resetPriorityButton: setPriority(.none)
        default: break
            
        }
        performSegue(withIdentifier: "done", sender: sender)
    }
    
    private func setPriority(_ person: PersonType) {
        print("Set prio: \(person)")
        NetworkManager.shared.send(message: Outbound.setPriority(person: person))
    }
    
    private func updateStyles() {
        setPriorityButton.layer.cornerRadius = UIGlobals.cardCornerRadius;
    }
    
    private func updateView() {
        setPriorityButton.isEnabled = currentPerson == .none
        resetPriorityButton.isEnabled = !(currentPerson == .none)
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
