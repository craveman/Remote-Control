//
//  PrioritySelectViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 11/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import Sm02Client

class PrioritySelectViewController: UIViewController {

    let rs = RemoteService.shared

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
        case setPriorityButton: rs.setPriority(for: (Bool.random() ? .left : .right))
        case resetPriorityButton: rs.setPriority(for: .none)
        default: break

        }
        performSegue(withIdentifier: "done", sender: sender)
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
