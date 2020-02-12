//
//  PointsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 03/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import SwiftUI

class PointsViewController: UIViewController {

  @IBOutlet weak var subView: UIView!

  override func viewDidLoad () {
    super.viewDidLoad()
  }

  @IBSegueAction func showPoints (_ coder: NSCoder)-> UIViewController? {
    let rootView = PointsSwiftUIView(showModal: false);
    return UIHostingController(coder: coder, rootView: rootView)
  }
}
