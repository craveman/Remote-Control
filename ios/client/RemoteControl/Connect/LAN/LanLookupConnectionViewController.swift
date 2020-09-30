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

fileprivate let WAIT_TIMEOUT = 9.0
fileprivate let NOTIFY_TIMEOUT = 2.5

class ManualConfiguration {
  static let MANUAL_CONNECTION = NSLocalizedString("lan_manual Manual connection", comment: "")
  static let SET_CONFIG = NSLocalizedString("lan_manual Enter connection config", comment: "")
  static let JOIN_BTN = NSLocalizedString("lan_manual Join", comment: "")
  static let CANCEL_BTN = NSLocalizedString("lan_manual Cancel", comment: "")
  static let IP_INPUT_PLACEHOLDER = NSLocalizedString("lan_manual Enter IP adress", comment: "")
}

class LanLookupConnectionViewController: UIViewController, ConnectionControllerProtocol {
  
  private var autoConnect = true {
    didSet {
      autoConnectSwitch.isOn = autoConnect
    }
  }
  private var isLookupStarted = false
  private var isConnecting = false {
    didSet {
      connectionProgressBar.setProgress(0, animated: false)
      connectionProgressBar.isHidden = !isConnecting
      if isConnecting {
        connectionProgressBar.setProgress(0.95, animated: true)
      }
      skipButton?.isEnabled = !isConnecting
      log("didSet isConnecting", isConnecting)
    }
  }
  private var reader: LanConfigReader = LanConfigReader()
  private var configSub: AnyCancellable?
  @IBOutlet weak var connectionProgressBar: UIProgressView!
  
  
  @IBAction func autoConnectSwitchChanged(_ sender: UISwitch) {
    print(sender.isOn)
    self.autoConnect = sender.isOn
  }
  @IBOutlet weak var autoConnectSwitch: UISwitch!
  
  @IBOutlet weak var serverFoundLabel: UILabel!
  
  @IBOutlet weak var connectionButton: UIButton!
  
  @IBAction func userAsksToConnect(_ sender: UIButton) {
    guard let config = reader.config else {
      print("userAsksToConnect: Emty config found")
      return
    }
    applyConfig(config)
  }
  
  @IBAction func manualConnectAction(_ sender: UIBarButtonItem) {
    log("Manual")
    autoConnect = false
    showInputDialog()
    stopScanner()
  }
  
  @IBAction func skipConnectAction(_ sender: UIBarButtonItem) {
    log("Skip")
    doConnectionCompletion(stopScan: !autoConnect)
  }
  
  @IBOutlet weak var skipButton: UIBarButtonItem!
  @IBOutlet weak var searchLabel: UILabel!
  @IBOutlet weak var failedToConnectLabel: UILabel!
  @IBOutlet weak var goToSettingsButton: UIButton!
  @IBOutlet weak var spinner: UIActivityIndicatorView!
  @IBAction func toSettings(_ sender: UIButton) {
    log("to Settings")
    DispatchQueue.main.async {
//      let wifiSettingsURL = URL(string:"App-Prefs:root=WIFI")
      let appSettingsURL = URL(string: UIApplication.openSettingsURLString)
      if let settingsURL = appSettingsURL {
        UIApplication.shared.open(settingsURL)
      }
    }
  }
  
  var isOnWiFiLookup: Bool = false
  
  var alert: UIAlertController? = nil
  
  var waitConnectTimer: Timer? = nil
  
  func onCloseManual(_ auto: Bool = true) -> Void {
    autoConnect = auto
    if autoConnect, let manConfig = reader.config {
      applyConfig(manConfig)
    }
    startScanner()
  }
  
  func setAutoConnectionMode(_ bool: Bool) -> Void {
    self.autoConnect = bool
  }
  
  func showInputDialog() {
    
    let alertController = UIAlertController(title: ManualConfiguration.MANUAL_CONNECTION, message: ManualConfiguration.SET_CONFIG, preferredStyle: .alert)
    
    let confirmAction = UIAlertAction(title: ManualConfiguration.JOIN_BTN, style: .default) { (_) in
      
      let name = alertController.textFields?[0].text
      
      guard let ip = name else {
        return
      }
      
      self.applyConfig(LanConfig(ip: ip, code: [0,0,0,0,0]))
      self.onCloseManual(false)
    }
    
    let cancelAction = UIAlertAction(title: ManualConfiguration.CANCEL_BTN, style: .cancel) { (_) in
      self.onCloseManual(self.autoConnectSwitch.isOn)
    }
    
    alertController.addTextField { (textField) in
      textField.placeholder = ManualConfiguration.IP_INPUT_PLACEHOLDER
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
    log("showAlert", alert)
    func applyAlert() {
      self.present(alert, animated: true, completion: {
        self.alert = alert
      })
    }
    
    if self.alert != nil {
      self.alert?.dismiss(animated: true, completion: {
        applyAlert()
      })
    } else {
      applyAlert()
    }
  }
  
  func startScanner() {
    log("Start scanner")
    toggleFailedCase(false)
    spinner.startAnimating()
    reader.startReader()
    withDelay({
      self.searchLabel.isHidden = false;
    }, NOTIFY_TIMEOUT)
    setWaitTimeout()
    isLookupStarted = true
  }
  
  func stopScanner() {
    if !isLookupStarted {
      log("scanner was not started")
      return
    }
    log("Stop scanner")
    toggleFailedCase(false)
    spinner.stopAnimating()
    waitConnectTimer?.invalidate()
    reader.stopReader()
    isLookupStarted = false
  }
  
  func doConnectionCompletion() {
    self.doConnectionCompletion(stopScan: true)
  }
  
  func doConnectionCompletion(stopScan: Bool = true) {
    log ("doConnectionCompletion")
    if stopScan {
      stopScanner()
    }
    successAction()
    withDelay({
      self.isConnecting = false
      self.connectionProgressBar.isHidden = true
    })
  }
  
  func doConnectionRejection() {
    print("doConnectionRejection")
    stopScanner()
    connectionProgressBar.isHidden = true
    isConnecting = false
    startScanner()
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
    }, WAIT_TIMEOUT)
  }
  
  func setConfigSubscription() -> Void {
    configSub = reader.$config.on(change: { config in
      log("config updated: \(String(describing: config))")
      guard let lan = config else {
        self.serverFoundLabel.isHidden = true
        self.connectionButton.isHidden = true
        log("$config.on(change nil")
        return
      }
      self.serverFoundLabel.isHidden = false
      self.connectionButton.isHidden = false
      self.toggleFailedCase(false)
      self.waitConnectTimer?.invalidate()
      guard self.autoConnect else {
        log("autoConnect if off")
        return
      }
      self.applyConfig(lan)
    })
  }
  
  func applyConfig(_ lan: LanConfig) -> Void {
    log("applyConfig", lan)
    guard !isConnecting else {
      log("applyConfig guard !isConnecting: exit", lan)
      return
    }
    isConnecting = true
    let connectionProcessor = ConnectionProcessor(controller: self)
    DispatchQueue.main.async {
      log("applyConfig: DispatchQueue.main.async")
      let remote = RemoteAddress(ssid: "", ip: lan.ip, code: lan.code)
      connectionProcessor.connectServer(to: remote)
    }
  }
  
  override func viewDidAppear (_ animated: Bool) {
    // todo:
    serverFoundLabel.isHidden = true
    connectionButton.isHidden = true
    autoConnectSwitch.isOn = autoConnect
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
