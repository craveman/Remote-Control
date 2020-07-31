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
  
  func showAlert(_ alert: UIAlertController) {
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
