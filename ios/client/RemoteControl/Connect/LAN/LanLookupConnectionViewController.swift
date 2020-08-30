//
//  LanLookupConnectionViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import UIKit
import class Combine.AnyCancellable

fileprivate func log(_ items: Any...) {
  print("LanLookupConnectionViewController:log: ", items)
}

class LanLookupConnectionViewController: UIViewController, ConnectionControllerProtocol {
  
  private var reader: LanConfigReader = LanConfigReader()
  private var configSub: AnyCancellable?
  
  @IBAction func manualConnectAction(_ sender: UIBarButtonItem) {
    log("Manual")
    showInputDialog()
  }
  
  @IBAction func skipConnectAction(_ sender: UIBarButtonItem) {
    log("Skip")
    doCompletion()
  }
  
  @IBOutlet weak var searchLabel: UILabel!
  @IBOutlet weak var failedToConnectLabel: UILabel!
  @IBOutlet weak var goToSettingsButton: UIButton!
  @IBOutlet weak var spinner: UIActivityIndicatorView!
  @IBAction func toSettings(_ sender: UIButton) {
    log("to Settings")
    DispatchQueue.main.async {
      if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
        UIApplication.shared.open(settingsURL)
      }
    }
  }
  
  var isOnWiFiLookup: Bool = false
  
  var alert: UIAlertController? = nil
  
  var waitConnectTimer: Timer? = nil
  
  func showInputDialog() {
    
    let alertController = UIAlertController(title: "Manual connection", message: "Enter connection config", preferredStyle: .alert)
    
    let confirmAction = UIAlertAction(title: "Join", style: .default) { (_) in
      
      let name = alertController.textFields?[0].text
      
      guard let ip = name else {
        return
      }
      
      self.applyConfig(LanConfig(ip: ip, code: [0,0,0,0,0]))
    }
    
    let cancelAction = UIAlertAction(title: "Cancel", style: .cancel) { (_) in }
    
    alertController.addTextField { (textField) in
      textField.placeholder = "Enter IP adress"
      textField.keyboardType = .numbersAndPunctuation
    }
    // code ???
    alertController.addAction(confirmAction)
    alertController.addAction(cancelAction)
    
    //finally presenting the dialog box
    self.present(alertController, animated: true, completion: nil)
    self.alert = alertController
  }
  
  func showAlert(_ alert: UIAlertController) {
    log("showAlert")
  }
  
  func startScanner() {
    log("Start scanner")
    toggleFailedCase(false)
    spinner.startAnimating()
    reader.startReader()
    withDelay({
      self.searchLabel.isHidden = false;
    }, 1.5)
    setWaitTimeout()
  }
  
  func stopScanner() {
    log("Stop scanner")
    toggleFailedCase(false)
    spinner.stopAnimating()
    reader.stopReader()
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
  
  func toggleFailedCase(_ on: Bool) -> Void {
    self.failedToConnectLabel.isHidden = !on
    self.goToSettingsButton.isHidden = !on
    self.searchLabel.isHidden = true
  }
  
  func setWaitTimeout() {
    waitConnectTimer?.invalidate()
    waitConnectTimer = withDelay({
      self.toggleFailedCase(true)
    }, 10)
  }
  
  func setConfigSubscription() -> Void {
    configSub = reader.$config.on(change: { config in
      log("config updated: \(String(describing: config))")
      guard let lan = config else {
        return
      }
      self.applyConfig(lan)
    })
  }
  
  func applyConfig(_ lan: LanConfig) -> Void {
    let remote = RemoteAddress(ssid: "", ip: lan.ip, code: lan.code)
    if case .failure(_) = rs.connection.connect(to: remote) {
      log("connect failure")
    }
  }
  
  override func viewDidAppear (_ animated: Bool) {
    // todo:
    toggleFailedCase(false)
    log("did appear")
    super.viewDidAppear(animated)
    setConfigSubscription()
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    log("viewWillDisappear")
    alert?.dismiss(animated: false, completion: {
      log("alert dismissed")
    })
    configSub?.cancel();
    waitConnectTimer?.invalidate();
    super.viewWillDisappear(animated)
  }
  
  override func viewDidDisappear (_ animated: Bool) {
    super.viewDidDisappear(animated)
    toggleFailedCase(false)
    DispatchQueue.main.async {
      self.stopScanner()
    }
  }
  
}
