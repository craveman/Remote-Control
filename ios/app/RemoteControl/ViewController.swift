//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

let nextSegue = "toConnetior";
let timeout = 1.4;

class ViewController: UIViewController {

  @IBOutlet weak var spinner: UIActivityIndicatorView!

  override func viewDidLoad() {
    stateRunner()

    super.viewDidLoad()
    // Do any additional setup after loading the view.
    spinner?.startAnimating();
    //do stuff
    Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) { timer in
      self.spinner?.stopAnimating();
      self.jumpToConnector();
    }
  }

  private func jumpToConnector() {
    performSegue(withIdentifier: nextSegue, sender: self)
  }
}

