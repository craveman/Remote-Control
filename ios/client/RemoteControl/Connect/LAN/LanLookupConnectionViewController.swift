//
//  LanLookupConnectionViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import UIKit
import Network
import class Combine.AnyCancellable

fileprivate func log(_ items: Any...) {
  print("LanLookupConnectionViewController:log: ", items)
}

fileprivate let WAIT_TIMEOUT = 9.0
fileprivate let NOTIFY_TIMEOUT = 2.5

class LanLookupConnectionViewController: UIViewController, ConnectionControllerProtocol {
  
  private var netWatcher: NetworkReachability? = nil
  
  private var autoConnect = false {
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
  private var optionsSub: AnyCancellable?
  @IBOutlet weak var connectionProgressBar: UIProgressView!
  
  
  @IBAction func autoConnectSwitchChanged(_ sender: UISwitch) {
    print("autoConnectSwitchChanged", sender.isOn)
    self.autoConnect = sender.isOn
  }
  @IBOutlet weak var autoConnectSwitch: UISwitch!
  
  @IBOutlet weak var serversFoundSubView: UIView!
  
  @IBOutlet weak var connectionButton: UIButton!
  
  @IBOutlet weak var autoConnectSwitchLabel: UIBarButtonItem!
  
  @IBAction func userAsksToConnect(_ sender: UIButton) {
    guard let config = reader.primaryConfig else {
      print("userAsksToConnect: Empty config found")
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
    if let next = reader.primaryConfig {
      applyConfig(next)
    }
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
      let appSettingsURL = URL(string: UIApplication.openSettingsURLString)
      if let settingsURL = appSettingsURL {
        UIApplication.shared.open(settingsURL)
      }
    }
  }
  
  var isOnWiFiLookup: Bool = false {
    didSet {
      print("isOnWiFiLookup didSet \(isOnWiFiLookup)")
      //      failedToConnectLabel.text = isOnWiFiLookup ? LanConnectionFails.NOT_FOUND : LanConnectionFails.NOT_VALID
      //      failedToConnectLabel.setNeedsDisplay()
    }
  }
  
  var alert: UIAlertController? = nil
  
  var waitConnectTimer: Timer? = nil
  
  func setAutoConnectionMode(_ bool: Bool) -> Void {
    print("setAutoConnectionMode \(bool)")
    self.autoConnect = bool
  }
  
  
  func onSuccess(_ action: @escaping () -> Void) {
    self.successAction = action
  }
  
  func toggleFailedCase(_ on: Bool) -> Void {
    self.failedToConnectLabel.isHidden = !on
    //    self.goToSettingsButton.isHidden = !on
//    self.searchLabel.isHidden = true
  }
  
  func setWaitTimeout() {
    waitConnectTimer?.invalidate()
    waitConnectTimer = withDelay({
      self.toggleFailedCase(true)
    }, WAIT_TIMEOUT)
  }
  
  override func viewDidAppear (_ animated: Bool) {
    // todo:
    self.netWatcher?.start()
    serversFoundSubView.isHidden = false
    connectionButton.isHidden = true
    autoConnectSwitch.isOn = autoConnect
    autoConnectSwitch.isHidden = true
    autoConnectSwitchLabel.isEnabled = false
    autoConnectSwitchLabel.tintColor = UIColor.clear
    toggleFailedCase(false)
    log("did appear")
    super.viewDidAppear(animated)
    setConfigSubscription()
    setOptionsSubscription()
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    log("viewWillDisappear")
    alert?.dismiss(animated: false, completion: {
      log("alert dismissed")
    })
    configSub?.cancel();
    optionsSub?.cancel();
    waitConnectTimer?.invalidate();
    super.viewWillDisappear(animated)
  }
  
  override func viewDidDisappear (_ animated: Bool) {
    super.viewDidDisappear(animated)
    toggleFailedCase(false)
    DispatchQueue.main.async {
      self.stopScanner()
    }
    self.netWatcher?.stop()
  }
  
  override func viewDidLoad () {
    super.viewDidLoad()
    
    initNetWatcher()
  }
  
  private func initNetWatcher() {
    let lanPathUpdateHandler: ((NWPath) -> Void) = {path in
      DispatchQueue.main.async {
        log("Asking permitions")
        checkLanPermission()
        
        guard let watcher = self.netWatcher else {
          self.toggleLanConnetionState(online: false)
          return;
        }
        self.toggleLanConnetionState(online: watcher.isNetworkAvailable())
        
      }
    }
    
    self.netWatcher = NetworkReachability(withHandler: lanPathUpdateHandler)
  }
  
  private func toggleLanConnetionState(online: Bool) -> Void {
    log("toggleLanConnetionState online: \(online)")
    isOnWiFiLookup = online
  }
  
  private var successAction: () -> Void = {
    log ("Not defined success action")
  }
}


extension LanLookupConnectionViewController {
  
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
  
}

extension LanLookupConnectionViewController {
  
  func setConfigSubscription() -> Void {
    // todo:
    configSub = reader.$primaryConfig.on(change: { config in
      log("$primaryConfig updated: \(String(describing: config))")
      guard let lan = config else {
//        self.serversFoundSubView.isHidden = true
//        self.connectionButton.isHidden = true
        log("$$primaryConfig.on(change nil")
        return
      }
//      self.serversFoundSubView.isHidden = false
//      self.connectionButton.isHidden = false
      self.toggleFailedCase(false)
      self.waitConnectTimer?.invalidate()
      guard self.autoConnect else {
        log("autoConnect if off")
        return
      }
      self.applyConfig(lan)
    })
  }
  
  func getConnectionOptionsList(_ opts: [LanConfigReaderOption]) -> [LanConfigReaderOption] {
    var vacant = opts.filter({ !$0.busy })
    var busy = opts.filter({ $0.busy })
    vacant.sort(by: {lhs, rhs in
      return lhs.name < rhs.name
    })
    busy.sort(by: {lhs, rhs in
      return lhs.name < rhs.name
    })
    var list: [LanConfigReaderOption] = []
    list.append(contentsOf: vacant)
    list.append(contentsOf: busy)
    return list;
  }
  
  func onOptionSelected(title: String) -> Void {
    func searchSelectedTitle (_ option: LanConfigReaderOption) -> Bool {
      if option.name == title {
        return true
      }
      return false
    }
    guard let config = self.reader.options.first(where: searchSelectedTitle) else {
      return
    }
    self.applyConfig(LanConfig(ip: config.address.ip, port: config.address.port, code: config.address.code))
  }
  
  func setOptionsSubscription() -> Void {
    
    guard let selector = self.serversFoundSubView.subviews[0].next as? LanOptionsSelector else {
      return
    }
    
    selector.onOptionSelected(self.onOptionSelected)
    // todo:
    optionsSub = reader.$options.on(change: { opts in
      
      guard let selector = self.serversFoundSubView.subviews[0].next as? LanOptionsSelector else {
        return
      }
      if(opts.count > 0) {
        self.waitConnectTimer?.invalidate()
      }
      selector.setOptions(self.getConnectionOptionsList(opts))
      
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
      let remote = RemoteAddress(ssid: "", ip: lan.ip, port: lan.port, code: lan.code)
      connectionProcessor.connectServer(to: remote)
    }
  }
  
}

extension LanLookupConnectionViewController {
  
  func startScanner() {
    log("Start LAN Lookup scanner")
    toggleFailedCase(false)
    spinner.startAnimating()
    reader.startReader()
    searchLabel.isHidden = false
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
}

extension LanLookupConnectionViewController {
  
  func onCloseManual(_ auto: Bool = true) -> Void {
    autoConnect = auto
    if autoConnect, let manConfig = reader.primaryConfig {
      applyConfig(manConfig)
    }
    startScanner()
  }
  
  func showInputDialog() {
    
    let alertController = UIAlertController(title: LanManualConfiguration.MANUAL_CONNECTION, message: LanManualConfiguration.SET_CONFIG, preferredStyle: .alert)
    
    let confirmAction = UIAlertAction(title: LanManualConfiguration.JOIN_BTN, style: .default) { (_) in
      
      let name = alertController.textFields?[0].text
      
      guard let ip = name else {
        return
      }
      
      self.applyConfig(LanConfig(ip: ip, port: 21074, code: [0,0,0,0,0]))
      self.onCloseManual(false)
    }
    
    let cancelAction = UIAlertAction(title: LanManualConfiguration.CANCEL_BTN, style: .cancel) { (_) in
      self.onCloseManual(self.autoConnectSwitch.isOn)
    }
    
    alertController.addTextField { (textField) in
      textField.placeholder = LanManualConfiguration.IP_INPUT_PLACEHOLDER
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
  
}
