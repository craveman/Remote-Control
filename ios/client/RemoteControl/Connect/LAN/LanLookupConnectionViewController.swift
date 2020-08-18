//
//  LanLookupConnectionViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import UIKit

fileprivate func log(_ items: Any...) {
  print("LanLookupConnectionViewController:log: ", items)
}

class LanLookupConnectionViewController: UIViewController, ConnectionControllerProtocol {
  
  private var reader: LanConfigReader = LanConfigReader()
  
  @IBAction func manualConnectAction(_ sender: UIBarButtonItem) {
    log("Manual")
  }
  
  @IBAction func skipConnectAction(_ sender: UIBarButtonItem) {
    log("Skip")
    doCompletion()
  }
  
  @IBOutlet weak var connectButton: UIButton!
  @IBOutlet weak var spinner: UIActivityIndicatorView!
  
  var isOnWiFiLookup: Bool = false
  
  var alert: UIAlertController? = nil
  
  func showAlert(_ alert: UIAlertController) {
    log("showAlert")
  }
  
  func startScanner() {
    log("Start scanner")
    spinner.startAnimating()
  }
  
  func stopScanner() {
    log("Stop scanner")
    spinner.stopAnimating()
  }
  
  func doCompletion() {
    log ("doCompletion")
    successAction()
  }
  
  private var successAction: () -> Void = {
    log ("Not defined success action")
  }
  
  func onSuccess(_ action: @escaping () -> Void) {
    self.successAction = action
  }
  
  
  override func viewDidAppear (_ animated: Bool) {
    // todo:
    super.viewDidAppear(animated)
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    log("viewWillDisappear")
    alert?.dismiss(animated: false, completion: {
      log("alert dismissed")
    })
    super.viewWillDisappear(animated)
  }
  
  override func viewDidDisappear (_ animated: Bool) {
    super.viewDidDisappear(animated)
    DispatchQueue.main.async {
      self.stopScanner()
      
    }
  }
  
}
