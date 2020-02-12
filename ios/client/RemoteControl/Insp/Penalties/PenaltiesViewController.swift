//
//  PenaltiesViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 04/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class PenaltiesViewController: UIViewController {
    
  let storyBoard = "PenaltiesStoryboard"
  let vcName = "PenaltiesCards"

  @IBOutlet weak var penaltiesSubView: UIView!
    
  lazy var penaltiesCtrl: PenaltiesCardsViewController = {
    let stbrd = UIStoryboard(name: storyBoard, bundle: nil)

    var vc = stbrd.instantiateViewController(withIdentifier: vcName) as! PenaltiesCardsViewController
    self.addViewControllerAsChildViewController(childViewController: vc)

    return vc
  }()
    
    
  lazy var pCardsCtrl: PenaltiesCardsViewController = {
    let stbrd = UIStoryboard(name: storyBoard, bundle: nil)

    var vc = stbrd.instantiateViewController(withIdentifier: vcName) as! PenaltiesCardsViewController

    self.addViewControllerAsChildViewController(childViewController: vc)
    return vc
  }()
    
  override func viewDidLoad () {
    super.viewDidLoad()
    updateView()
    // Do any additional setup after loading the view.
  }

  private func updateView () {
    penaltiesCtrl.view.isHidden = false
    pCardsCtrl.view.isHidden = true

    penaltiesCtrl.setType(.basic)
    pCardsCtrl.setType(.passive)
  }

  private func addViewControllerAsChildViewController (childViewController: UIViewController) {
    addChild(childViewController)

    penaltiesSubView.addSubview(childViewController.view)
    childViewController.view.frame = penaltiesSubView.bounds
    childViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]

    childViewController.didMove(toParent: self)
  }
}
