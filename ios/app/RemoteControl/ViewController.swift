//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

let networksSegue = "toNetworks";
let timeout = 2.2;

class ViewController: UIViewController {

  @IBOutlet weak var spinner: UIActivityIndicatorView!

  override func viewDidLoad() {
    super.viewDidLoad()
    // Do any additional setup after loading the view.
    spinner.startAnimating();
    //do stuff
    Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) { timer in
      self.spinner.stopAnimating();
    // when ready to select netowks
      self.jumpToNetworkSelect();
    }
  }

  private func jumpToNetworkSelect() {
    performSegue(withIdentifier: networksSegue, sender: self)
  }
}

