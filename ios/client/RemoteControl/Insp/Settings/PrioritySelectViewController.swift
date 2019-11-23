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

  var currentPerson: PersonType = .none

  @IBOutlet weak var setPriorityButton: UIButton!

  @IBOutlet weak var resetPriorityButton: UIButton!

  override func viewDidLoad () {
    super.viewDidLoad()

    updateStyles()

    [setPriorityButton, resetPriorityButton].forEach({ btn in
      btn?.addTarget(self, action: Selector(("setPriorityAction:")), for: .touchUpInside)
    })

    updateView()
    // Do any additional setup after loading the view.
  }

  @objc func setPriorityAction (_ sender: UIButton) {
    switch sender {
    case setPriorityButton:
      if Bool.random() {
        rs.persons.left.setPriority()
      } else {
        rs.persons.right.setPriority()
      }
    case resetPriorityButton:
      rs.persons.resetPriority()
    default:
      break
    }
    performSegue(withIdentifier: "done", sender: sender)
  }

  private func updateStyles () {
    setPriorityButton.layer.cornerRadius = UIGlobals.cardCornerRadius
  }

  private func updateView () {
    setPriorityButton.isEnabled = currentPerson == .none
    resetPriorityButton.isEnabled = !(currentPerson == .none)
  }
}
