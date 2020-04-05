//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import QRCodeReader
import NetworkExtension
import SystemConfiguration.CaptiveNetwork

fileprivate func log(_ items: Any...) {
  print("QrViewController:log: ", items)
}

class QrViewController: UIViewController {
  
  public static let SCANNER = "QR scanner"
  public static let SCAN_SUCCESS = "The code is recognized"
  public static let SCAN_FAIL = "The code is unrecognized"
  public static let CONNECT = "Connect Sm02"
  public static let CONNECT_OPEN_WIFI = "Connect Open WiFi first"
  public static let CONNECT_PROTECTED_WIFI = "Connect Protected WiFi first"
  public static let RECONNECT = "Try again"
  public static let ERROR = "Connection error"
  public var isOnWiFiLookup = false {
    didSet {
      if isOnWiFiLookup {
        spinner.isHidden = false
        spinner.startAnimating()
      } else {
        spinner.stopAnimating()
        spinner.isHidden = true
      }
    }
  }
  
  @IBOutlet weak var spinner: UIActivityIndicatorView! {
    didSet {
      spinner.stopAnimating()
      spinner.isHidden = true
    }
  }
  
  @IBOutlet weak var previewView: QRCodeReaderView! {
    didSet {
      previewView.setupComponents(with: QRCodeReaderViewControllerBuilder {
        $0.reader = reader
        $0.showTorchButton = false
        $0.showSwitchCameraButton = false
        $0.showCancelButton = false
        $0.showOverlayView = true
        $0.rectOfInterest = CGRect(x: 0.2, y: 0.2, width: 0.6, height: 0.6)
      })
    }
  }
  
  @IBAction func skipButtonClicked(_ sender: UIButton) {
    self.onSuccess()
  }
  
  lazy var reader: QRCodeReader = QRCodeReader()
  private var qrCodeProcessor: QrCodeProcessor? = nil
  var alert: UIAlertController?
  var onSuccess: (() -> Void) = { log ("Not defined success action") }
  
  
  override func viewDidAppear (_ animated: Bool) {
    self.qrCodeProcessor = QrCodeProcessor(controller: self)
    super.viewDidAppear(animated)
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    log("viewWillDisappear")
    dismissAlert()
    super.viewWillDisappear(animated)
  }
  
  override func viewDidDisappear (_ animated: Bool) {
    super.viewDidDisappear(animated)
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
  }
  
  public func startScanner () {
    let scanPermissionsChecker = ScanPermissionsChecker(controller: self)
    guard scanPermissionsChecker.check(), !reader.isRunning else {
      return
    }
    
    reader.didFindCode = { [weak self] (result) in
      guard let self = self else {
        return
      }
      guard self.qrCodeProcessor != nil else {
        return
      }
      self.reader.stopScanning()
      
      Vibration.on()
      
      switch RemoteAddress.parse(urlString: result.value) {
      case .success(let remote):
        self.qrCodeProcessor!.on(success: remote)
      case .failure(let error):
        self.qrCodeProcessor!.on(failure: error)
      }
    }
    
    if (self.isOnWiFiLookup) {
      return
    }
    
    reader.startScanning()
  }
  
  public func stopScanner () {
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
  }
  
  fileprivate func showAlert(_ alert: UIAlertController) {
    dismissAlert()
    self.alert = alert
    present(alert, animated: true, completion: {
      log("alert present completed: " + (alert.message ?? "empty alert"))
    })
  }
  
  private func dismissAlert() {
    log("try dismiss alert")
    guard self.alert != nil else {
      return
    }
    log("dismissing: " + ( alert!.message ?? "-empty-" ) )
    self.alert!.dismiss(animated: false)
    self.alert = nil
    log("alert dismissed")
  }
  
  private struct QrCodeProcessor {
    
    let controller: QrViewController
    
    func on (success remote: RemoteAddress) {
      log("parsed \(remote)")
      let connectionProcessor = ConnectionProcessor(controller: controller)
      
      func complete() {
        connectionProcessor.connectServer(to: remote)
      }
      
      func protectedNetworkTry() {
        connectionProcessor.askForWiFiPass(completionHandler: { (pass) in
          let password = pass.count > 0 ? pass : nil
          connectionProcessor.connectHotspot(remote.ssid, passoword: password) { connected in
            guard connected else {
              self.controller.present(alert, animated: true, completion: nil)
              return
            }
            
            complete()
          }
        })
      }
      
      func openNetworkTry() {
        connectionProcessor.connectHotspot(remote.ssid) { connected in
          guard connected else {
            self.controller.present(alert, animated: true, completion: nil)
            return
          }
          
          complete()
        }
      }
      let titleString = remote.ssid  // NSLocalizedString(SCANNER, comment: "")
      let bodyString = NSLocalizedString(SCAN_SUCCESS, comment: "")
      let okString = NSLocalizedString(CONNECT, comment: "")
      let open = NSLocalizedString(CONNECT_OPEN_WIFI, comment: "")
      let secured = NSLocalizedString(CONNECT_PROTECTED_WIFI, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .actionSheet
      )
      
      alert.addAction(UIAlertAction(title: okString, style: .cancel, handler: { action in
        complete()
      }))
      
      alert.addAction(UIAlertAction(title: open, style: .default, handler: { action in
        openNetworkTry()
      }))
      
      alert.addAction(UIAlertAction(title: secured, style: .default, handler: { action in
        protectedNetworkTry()
      }))
      
      controller.present(alert, animated: true)
    }
    
    func on (failure error: Error) {
      log("error \(error)")
      
      let titleString = NSLocalizedString(SCANNER, comment: "")
      let bodyString = NSLocalizedString(SCAN_FAIL, comment: "")
      let tryAgainButtonString = NSLocalizedString(RECONNECT, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      alert.addAction(UIAlertAction(title: tryAgainButtonString, style: .cancel, handler: { action in
        self.controller.reader.startScanning()
      }))
      
      controller.present(alert, animated: true, completion: nil)
    }
  }
  
  private struct ConnectionProcessor {
    //     todo: ask user for location -> WiFi list reading
    private let hasWiFiReadingPermition = false
    
    public static let CONNECTION_FAILED = "Can't connect to %@. %@"
    
    public static let WRONG_CODE = "Wrong authentication code."
    public static let BUSY = "Another remote control is already registered on the server."
    public static let TIMEOUT = "Connection timeout. Check that you are connected to the '%@' Wi-Fi network."
    public static let REFUSED = "A remote server is not reachable."
    public static let UNKNOWN = "Unknown connection error: %@"
    public static let PROVIDE_PASSWORD = "Please provide password"
    public static let PASSWORD_PRIVACY = "We never save or share your data"
    public static let PROCEED = "Proceed"
    
    let controller: QrViewController
    
    func currentSSIDs() -> [String?] {
      guard let interfaceNames = CNCopySupportedInterfaces() as? [String] else {
        return []
      }
      return interfaceNames.compactMap { name in
        guard let info = CNCopyCurrentNetworkInfo(name as CFString) as? [String:AnyObject] else {
          return nil
        }
        guard let ssid = info[kCNNetworkInfoKeySSID as String] as? String else {
          return nil
        }
        return ssid
      }
    }
    
    func askForWiFiPass(completionHandler: ((String) -> Void)? = nil) {
      let title = NSLocalizedString(ConnectionProcessor.PROVIDE_PASSWORD, comment: "")
      let message = NSLocalizedString(ConnectionProcessor.PASSWORD_PRIVACY, comment: "")
      let joinButtonText = NSLocalizedString(ConnectionProcessor.PROCEED, comment: "")
      let alert = UIAlertController(
        title: title,
        message: message,
        preferredStyle: .alert
      )
      var passInput: UITextField? = nil
      
      alert.addTextField { (field) in
        log(field)
        field.isSecureTextEntry = true
        field.passwordRules = UITextInputPasswordRules(descriptor: "allowed: upper, lower, digit, [-().&@?’#,/&quot;+]; minlength: 8;")
        field.autocorrectionType = .no
        field.autocapitalizationType = .none
        field.keyboardType = .namePhonePad
        
        field.returnKeyType = .join
        passInput = field
      }
            
      alert.addAction(UIAlertAction(title: joinButtonText, style: .default, handler: { action in
        log("Proceed", passInput?.text ?? "<empty>")
        if completionHandler != nil{
          completionHandler!(passInput?.text ?? "")
        }
      }))
      
      controller.present(alert, animated: true, completion: nil)
    }
    
    func connectHotspot(_ ssid: String, passoword pass: String? = nil, joinOnce once: Bool = true, isWEP wep: Bool = false, completionHandler: ((Bool) -> Void)? = nil) {
      
      var configuration: NEHotspotConfiguration
      if (pass == nil) {
        configuration = NEHotspotConfiguration.init(ssid: ssid)
      } else {
        configuration = NEHotspotConfiguration.init(ssid: ssid, passphrase: pass!, isWEP: wep)
      }
      
      controller.isOnWiFiLookup = true
      
      configuration.joinOnce = once
      //      NEHotspotConfigurationManager.shared.removeConfiguration(forSSID: ssid)
      NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
        
        self.controller.isOnWiFiLookup = false
        
        var connected = false
        if error != nil {
          if (error! as NSError).code == NEHotspotConfigurationError.alreadyAssociated.rawValue {
            log("Already Connected", error ?? "NO_ERROR")
            connected = true
          }
          else if (error! as NSError).code == NEHotspotConfigurationError.userDenied.rawValue {
            log("User Denied", error ?? "NO_ERROR")
          }
          else {
            log("Not Connected", error ?? "NO_ERROR")
          }
        }
        else {
          if self.hasWiFiReadingPermition {
            let list = self.currentSSIDs()
            
            log("currentSSIDs:", list)
            connected = list.first == ssid;
          } else {
            connected = true
          }
          
          log("Connected:", connected)
        }
        if completionHandler != nil {
          completionHandler!(connected)
        }
      }
    }
    
    func connectServer (to remote: RemoteAddress) {
      
      let result = rs.connection.connect(to: remote)
      
      switch result {
      case .success(AuthenticationStatus.success):
        controller.onSuccess()
      case .success(AuthenticationStatus.wrongAuthenticationCode):
        let message = NSLocalizedString(ConnectionProcessor.WRONG_CODE, comment: "")
        showError(remote, message)
      case .success(AuthenticationStatus.alreadyRegistered):
        let message = NSLocalizedString(ConnectionProcessor.BUSY, comment: "")
        showError(remote, message)
      case .failure(ConnectionError.responseTimeout(_)):
        fallthrough
      case .failure(ConnectionError.connectionTimeout(_)):
        let message = String(
          format: NSLocalizedString(ConnectionProcessor.TIMEOUT, comment: ""),
          remote.ssid
        )
        showError(remote, message)
      case .failure(ConnectionError.connectionRefused):
        let message = NSLocalizedString(ConnectionProcessor.REFUSED, comment: "")
        showError(remote, message)
      case let .failure(error):
        let message = String(
          format: NSLocalizedString(ConnectionProcessor.UNKNOWN, comment: ""),
          "\(error)"
        )
        showError(remote, message)
      }
    }
    
    private func showError (_ remote: RemoteAddress, _ text: String) {
      let titleString = NSLocalizedString(ERROR, comment: "")
      let bodyString = String(
        format: NSLocalizedString(ConnectionProcessor.CONNECTION_FAILED, comment: ""),
        remote.ip, text
      )
      let tryAgainButtonString = NSLocalizedString(RECONNECT, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      alert.addAction(UIAlertAction(title: tryAgainButtonString, style: .cancel, handler: { action in
        self.controller.reader.startScanning()
      }))
      controller.showAlert(alert)
    }
  }
  
  private struct ScanPermissionsChecker {
    public static let SCANNER_ERROR = "QR scanner error"
    public static let TO_SETTINGS = "Settings"
    public static let PROCEED = "Ok"
    
    public static let UNAUTHORIZED = "The app is not authorized to use back camera."
    public static let UNSUPPORTED = "QR scanner is not supported by the current device."
    
    let controller: QrViewController
    
    func check () -> Bool {
      do {
        return try QRCodeReader.supportsMetadataObjectTypes()
      } catch let error as NSError {
        switch error.code {
        case -11852:
          handleNotAuthorizedToUseBackCamera()
        default:
          handleReaderNotSupported()
        }
      }
      return false
    }
    
    private func handleNotAuthorizedToUseBackCamera () {
      let titleString = NSLocalizedString(ScanPermissionsChecker.SCANNER_ERROR, comment: "")
      let bodyString = NSLocalizedString(ScanPermissionsChecker.UNAUTHORIZED, comment: "")
      let settingsButtonString = NSLocalizedString(ScanPermissionsChecker.TO_SETTINGS, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      alert.addAction(UIAlertAction(title: settingsButtonString, style: .default, handler: { (_) in
        DispatchQueue.main.async {
          if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(settingsURL)
          }
        }
      }))
      controller.showAlert(alert)
    }
    
    private func handleReaderNotSupported () {
      let titleString = NSLocalizedString(ScanPermissionsChecker.SCANNER_ERROR, comment: "")
      let bodyString = NSLocalizedString(ScanPermissionsChecker.UNSUPPORTED, comment: "")
      let okButtonString = NSLocalizedString(ScanPermissionsChecker.PROCEED, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      alert.addAction(UIAlertAction(title: okButtonString, style: .cancel, handler: nil))
      log("handleReaderNotSupported")
      controller.showAlert(alert)
      
    }
  }
}
