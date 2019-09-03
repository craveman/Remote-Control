//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
//import networking
import logging

let networksSegue = "toNetworks";
let timeout = 1.4;

class ViewController: UIViewController {

  @IBOutlet weak var spinner: UIActivityIndicatorView!

  override func viewDidLoad() {
//    let outbound = Outbound.swap

    let logger = LoggerFactory.create()
    logger.info("Hello world")

    super.viewDidLoad()
    // Do any additional setup after loading the view.
    spinner?.startAnimating();
    //do stuff
    Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) { timer in
      self.spinner?.stopAnimating();
    // when ready to select netowks
      self.jumpToNetworkSelect();
    }
  }

  private func jumpToNetworkSelect() {
    performSegue(withIdentifier: networksSegue, sender: self)
  }
}

