//
//  ConnectionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 10/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import Sm02Client

class ConnectionsViewController: UIViewController, UIAdaptivePresentationControllerDelegate {
    
  @IBOutlet weak var qrReaderSubViewWrapper: UIView!
    
  override func viewDidAppear (_ animated: Bool) {
    let scanner = getScanner()
    scanner.onSuccess = { [weak self] in
      self?.jumpToInspiration()
    }
    
    if isSimulationEnv() {
      skipQR()
    }
    super.viewDidAppear(animated)
  }
    
  private func getScanner () -> QrViewController {
    return qrReaderSubViewWrapper.subviews[0].next as! QrViewController
  }
    
  override func prepare (for segue: UIStoryboardSegue, sender: Any?) {
    if segue.identifier == "skipQR" {
      segue.destination.presentationController?.delegate = (self as UIAdaptivePresentationControllerDelegate)
    }
    if isSimulationEnv() {
      return
    }
    if let qrReader = qrReaderSubViewWrapper.next as? QrViewController {
      qrReader.stopScanner()
    }
  }
    
  func presentationControllerDidDismiss (_ presentationController: UIPresentationController) {
    rs.connection.disconnect()
    if isSimulationEnv() {
      skipQR()
      return
    }
    getScanner().startScanner()
  }
  
  private func jumpToInspiration () {
    performSegue(withIdentifier: "skipQR", sender: nil)
  }
    
  private func isSimulationEnv () -> Bool {
    #if targetEnvironment(simulator)
      return true
    #else
      return false
    #endif
  }

  private func skipQR () {
    Utils.delay({
      self.jumpToInspiration()
    }, seconds: 1)
  }
}
