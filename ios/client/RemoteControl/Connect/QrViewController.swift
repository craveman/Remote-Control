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

class QrViewController: UIViewController {
  
  public static let SCANNER = "QR scanner"
  public static let SCAN_SUCCESS = "The code is recognized"
  public static let SCAN_FAIL = "The code is unrecognized"
  public static let CONNECT = "Connect"
  public static let RECONNECT = "Try again"
  public static let ERROR = "Connection error"
  
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
  var onSuccess: (() -> Void) = { print ("Not defined success action") }
  
  fileprivate func dismissAlert() {
    print("dismiss alert")
    guard self.alert != nil else {
      return
    }
    
    self.alert!.dismiss(animated: false)
    self.alert = nil
  }
  
  override func viewDidAppear (_ animated: Bool) {
    self.qrCodeProcessor = QrCodeProcessor(controller: self)
    super.viewDidAppear(animated)
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    print("viewWillDisappear")
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
    
    reader.startScanning()
  }
  
  public func stopScanner () {
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
  }
  
  private struct QrCodeProcessor {
    
    let controller: QrViewController
    
    func on (success remote: RemoteAddress) {
      print("parsed \(remote)")
      
      
      let titleString = NSLocalizedString(SCANNER, comment: "")
      let bodyString = NSLocalizedString(SCAN_SUCCESS, comment: "")
      let okString = NSLocalizedString(CONNECT, comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      let connectionProcessor = ConnectionProcessor(controller: controller)
      
      
      
      connectionProcessor.connectHotspot(remote.ssid) { connected in
        guard connected else {
          alert.addAction(UIAlertAction(title: okString, style: .cancel, handler: { action in
            connectionProcessor.connectServer(to: remote)
          }))
          
          self.controller.present(alert, animated: true, completion: nil)
          return
        }
        
        connectionProcessor.connectServer(to: remote)
      }
      
    }
    
    func on (failure error: Error) {
      print("error \(error)")
      
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
    private let hasWiFiReadingPermition = true
    
    public static let CONNECTION_FAILED = "Can't connect to %@. %@"
    
    public static let WRONG_CODE = "Wrong authentication code."
    public static let BUSY = "Another remote control is already registered on the server."
    public static let TIMEOUT = "Connection timeout. Check that you are connected to the '%@' Wi-Fi network."
    public static let REFUSED = "A remote server is not reachable."
    public static let UNKNOWN = "Unknown connection error: %@"
    
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
    
    func connectHotspot(_ ssid: String, passoword pass: String? = nil, joinOnce once: Bool = true, isWEP wep: Bool = false, completionHandler: ((Bool) -> Void)? = nil) {
      var configuration: NEHotspotConfiguration
      if (pass == nil) {
        configuration = NEHotspotConfiguration.init(ssid: ssid)
      } else {
        configuration = NEHotspotConfiguration.init(ssid: ssid, passphrase: pass!, isWEP: wep)
      }
      
      configuration.joinOnce = once
      //      NEHotspotConfigurationManager.shared.removeConfiguration(forSSID: ssid)
      NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
        var connected = false
        if error != nil {
          if (error! as NSError).code == NEHotspotConfigurationError.alreadyAssociated.rawValue {
            print("Already Connected", error)
            connected = true
          }
          else if (error! as NSError).code == NEHotspotConfigurationError.userDenied.rawValue {
            print("User Denied", error)
          }
          else {
            print("Not Connected", error)
          }
        }
        else {
          
          if self.hasWiFiReadingPermition {
            let list = self.currentSSIDs()
            
            print("currentSSIDs:", list)
            connected = list.first == ssid;
          } else {
            connected = true
          }
          
          print("Connected:", connected)
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
      
      controller.dismissAlert()
      controller.alert = alert
      controller.present(alert, animated: true, completion: nil)
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
      print("handleReaderNotSupported")
      controller.dismissAlert()
      controller.alert = alert
      controller.present(alert, animated: true, completion: nil)
    }
  }
}
