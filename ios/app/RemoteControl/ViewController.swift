//
//  ViewController.swift
//  RemoteControl
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit


class ViewController: UIViewController {
  
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
  
  override func viewDidAppear (_ animated: Bool) {
    super.viewDidAppear(animated)
    
    guard checkScanPermissions(), !reader.isRunning else {
      return
    }
    
    reader.didFindCode = { [weak self] (result) in
      print("Completion with result: \(result.value) of type \(result.metadataType)")
      self?.process(scanned: result)
    }
    
    reader.startScanning()
  }
  
  private func process (scanned result: QRCodeReaderResult) {
    reader.stopScanning()
    
    let alert = UIAlertController(
      title: "QRCodeReader",
      message: String (format:"%@ (of type %@)", result.value, result.metadataType),
      preferredStyle: .alert
    )
    
    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [weak self] (action) in
      self?.performSegue(withIdentifier: "toInspiration", sender: nil)
    }))
    
    present(alert, animated: true, completion: nil)
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
      }
      
      present(alert, animated: true, completion: nil)
      return false
    }
  }
}

