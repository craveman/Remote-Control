//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import RemoteControl_Network
import RemoteControl_Logging

class ViewController: UIViewController {

  override func viewDidLoad() {
    let outbound = Outbound.swap

    let logger = LoggerFactory.create()
    logger.info("Hello world")

    super.viewDidLoad()
    // Do any additional setup after loading the view.
  }
}

