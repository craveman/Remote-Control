//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit


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
  var openedAlertCallback: (() -> Void)?
  override func viewDidAppear (_ animated: Bool) {
    super.viewDidAppear(animated)
    
    guard checkScanPermissions(), !reader.isRunning else {
      return
    }
    
    reader.didFindCode = { [weak self] (result) in
      print("Completion with result: \(result.value) of type \(result.metadataType)")
      guard let self = self else {
        return
      }
      self.reader.stopScanning()
      
      switch ServerAccess.parse(url: result.value) {
      case .success(let access):
        self.process(success: access)
      case .failure(let reason):
        self.process(error: reason)
      }
    }
    
    reader.startScanning()
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    if openedAlertCallback != nil {
      openedAlertCallback!()
      openedAlertCallback = nil
    }
    super.viewWillDisappear(animated)
  }
  
  override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    DispatchQueue.main.async {
      self.reader.stopScanning()
    }
    
  }
  
  private func process (success access: ServerAccess) {
    let alert = UIAlertController(
      title: "QR-код распознан",
      message: "\(access)",
      preferredStyle: .alert
    )
    
    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [weak self] (action) in
      self?.performSegue(withIdentifier: "toInspiration", sender: nil)
    }))
    
    present(alert, animated: true, completion: nil)
//    self.bindAlert(alert)
  }
  
  private func process (error: ServerAccess.ParsingError) {
    let alert = UIAlertController(
      title: "QR-код не распознан",
      message: "\(error)",
      preferredStyle: .alert
    )
    
    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [weak self] (action) in
      self?.reader.startScanning()
    }))
    
    present(alert, animated: true, completion: nil)
//    self.bindAlert(alert)
  }
  
  private func bindAlert(_ alert: UIAlertController?) {
    openedAlertCallback = {
      alert?.dismiss(animated: false)
    }
  }
  
  private func checkScanPermissions () -> Bool {
    do {
      return try QRCodeReader.supportsMetadataObjectTypes()
    } catch let error as NSError {
      let alert: UIAlertController
      
      switch error.code {
      case -11852:
        alert = UIAlertController(title: "Error", message: "This app is not authorized to use Back Camera.", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Setting", style: .default, handler: { (_) in
          DispatchQueue.main.async {
            if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
              UIApplication.shared.openURL(settingsURL)
            }
          }
        }))
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
      default:
        alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
        self.bindAlert(alert)
      }
      
      present(alert, animated: true, completion: nil)
      return false
    }
  }
}