//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit
import QRCodeReader

class QrViewController: UIViewController {
  
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
  
  lazy var reader: QRCodeReader = QRCodeReader()
  private var qrCodeProcessor: QrCodeProcessor? = nil
  var openedAlertCallback: (() -> Void)?
  var onSuccess: (() -> Void) = { print ("Not defined success action") }
  
  override func viewDidAppear (_ animated: Bool) {
    self.qrCodeProcessor = QrCodeProcessor(controller: self)
    super.viewDidAppear(animated)
    startScanner()
  }
  
  override func viewWillDisappear (_ animated: Bool) {
    if openedAlertCallback != nil {
      openedAlertCallback!()
      openedAlertCallback = nil
    }
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
      let proc = self.qrCodeProcessor!
      switch RemoteAddress.parse(urlString: result.value) {
      case .success(let remote):
        proc.on(success: remote)
      case .failure(let error):
        proc.on(failure: error)
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
      
      let titleString = NSLocalizedString("QR scanner", comment: "")
      let bodyString = NSLocalizedString("The code is recognized", comment: "")
      let okString = NSLocalizedString("Connect", comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      let connectionProcessor = ConnectionProcessor(controller: controller)
      alert.addAction(UIAlertAction(title: okString, style: .cancel, handler: { action in
        connectionProcessor.connect(to: remote)
      }))
      
      controller.present(alert, animated: true, completion: nil)
    }
    
    func on (failure error: Error) {
      print("error \(error)")
      
      let titleString = NSLocalizedString("QR scanner", comment: "")
      let bodyString = NSLocalizedString("The code is unrecognized", comment: "")
      let tryAgainButtonString = NSLocalizedString("Try again", comment: "")
      
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
    
    let controller: QrViewController
    
    func connect (to remote: RemoteAddress) {
      let result = rs.connection.connect(to: remote)
      
      switch result {
      case .success(AuthenticationStatus.success):
        controller.onSuccess()
      case .success(AuthenticationStatus.wrongAuthenticationCode):
        let message = NSLocalizedString("Wrong authentication code.", comment: "")
        showError(remote, message)
      case .success(AuthenticationStatus.alreadyRegistered):
        let message = NSLocalizedString("Another remote control is already registered on the server.", comment: "")
        showError(remote, message)
      case .failure(ConnectionError.responseTimeout(_)):
        fallthrough
      case .failure(ConnectionError.connectionTimeout(_)):
        let message = String(
          format: NSLocalizedString("Connection timeout. Check that you are connected to the '%@' Wi-Fi network.", comment: ""),
          remote.ssid
        )
        showError(remote, message)
      case .failure(ConnectionError.connectionRefused):
        let message = NSLocalizedString("A remote server is not reachable.", comment: "")
        showError(remote, message)
      case let .failure(error):
        let message = String(
          format: NSLocalizedString("Unknown connection error: %@", comment: ""),
          "\(error)"
        )
        showError(remote, message)
      }
    }
    
    private func showError (_ remote: RemoteAddress, _ text: String) {
      let titleString = NSLocalizedString("Connection error", comment: "")
      let bodyString = String(
        format: NSLocalizedString("Can't connect to %@. %@", comment: ""),
        remote.ip, text
      )
      let tryAgainButtonString = NSLocalizedString("Try again", comment: "")
      
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
      let titleString = NSLocalizedString("QR scanner error", comment: "")
      let bodyString = NSLocalizedString("The app is not authorized to use back camera.", comment: "")
      let settingsButtonString = NSLocalizedString("Settings", comment: "")
      
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
      
      controller.present(alert, animated: true, completion: nil)
    }
    
    private func handleReaderNotSupported () {
      let titleString = NSLocalizedString("QR scanner error", comment: "")
      let bodyString = NSLocalizedString("QR scanner is not supported by the current device.", comment: "")
      let okButtonString = NSLocalizedString("Ok", comment: "")
      
      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      
      alert.addAction(UIAlertAction(title: okButtonString, style: .cancel, handler: nil))
      
      controller.openedAlertCallback = {
        alert.dismiss(animated: false)
      }
      
      controller.present(alert, animated: true, completion: nil)
    }
  }
}
