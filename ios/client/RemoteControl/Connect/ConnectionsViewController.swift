//
//  ConnectionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 10/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit
import AVFoundation

class ConnectionsViewController: UIViewController, UIAdaptivePresentationControllerDelegate {
  public static let RECONNECT = "Reconnect"
  public static let CONNECTION_LOST = "Connection to the server in Wi-Fi network '%@' was lost."
  public static let CONNECTION_ERROR = "Connection error"
  var alert: UIAlertController?
  var performed = false
  let segueName = "jumpToInspiration"
  @IBOutlet weak var qrReaderSubViewWrapper: UIView!
  
  override func viewDidAppear (_ animated: Bool) {
    print("did appear")
    getScanner()?.onSuccess = { [weak self] in
      print("scanner on success", self == self)
      guard self == self else {
        return
      }
      self?.jumpToInspiration()
    }

    if isSimulationEnv() {
      AVCaptureDevice.requestAccess(for: AVMediaType.video) {_ in}
      performed = false
    } else {
      start()
    }
    
    super.viewDidAppear(animated)
  }

  private func getScanner () -> QrViewController? {
    guard qrReaderSubViewWrapper.subviews.count > 0 else {
      return nil
    }
    return qrReaderSubViewWrapper.subviews[0].next as? QrViewController
  }

  override func prepare (for segue: UIStoryboardSegue, sender: Any?) {
    guard qrReaderSubViewWrapper.subviews.count > 0 else {
      return
    }
    if segue.identifier == segueName {
      segue.destination.presentationController?.delegate = (self as UIAdaptivePresentationControllerDelegate)
    }
    if isSimulationEnv() {
      return
    }
    if let qrReader = qrReaderSubViewWrapper.subviews[0].next as? QrViewController {
      qrReader.stopScanner()
    }
  }

  func presentationControllerDidDismiss (_ presentationController: UIPresentationController) {
    start()
  }

  func warning (_ remote: RemoteAddress) {
    let bodyString = String(
      format: NSLocalizedString(ConnectionsViewController.CONNECTION_LOST, comment: ""),
      remote.ssid
    )
    prepareAlert(bodyString)
    present(self.alert!, animated: true, completion: nil)
  }

  func start () {
    performed = false
    if (rs.connection.isConnected) {
      rs.connection.disconnect()
    }
    
    getScanner()?.startScanner()
  }
  
  private func prepareAlert(_ bodyString: String) {
    
    guard self.alert != nil else {
      let titleString = NSLocalizedString(ConnectionsViewController.CONNECTION_ERROR, comment: "")
      
      let tryAgainButtonString = NSLocalizedString(ConnectionsViewController.RECONNECT, comment: "")

      let alert = UIAlertController(
        title: titleString,
        message: bodyString,
        preferredStyle: .alert
      )
      alert.addAction(UIAlertAction(title: tryAgainButtonString, style: .cancel, handler: { [weak self] (action) in
        self?.start()
      }))
      
      self.alert = alert
      return;
    }
    
    self.alert?.message = bodyString
    
  }

  func jumpToInspiration () {
    getScanner()?.stopScanner()
//    if (performed) {
//      return
//    }
    performSegue(withIdentifier: segueName, sender: nil)
    performed = true
  }

  private func isSimulationEnv () -> Bool {
    #if targetEnvironment(simulator)
      return true
    #else
      return false
    #endif
  }

  private func skipQR () {
    Utils.delay({
      self.jumpToInspiration()
    }, seconds: 1)
  }
}
