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

  @IBOutlet weak var qrReaderSubViewWrapper: UIView!

  override func viewDidAppear (_ animated: Bool) {
    getScanner()?.onSuccess = { [weak self] in
      guard self == self else {
        return
      }
      self?.jumpToInspiration()
    }

    if isSimulationEnv() {
      AVCaptureDevice.requestAccess(for: AVMediaType.video) {_ in}
      skipQR()
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
    if segue.identifier == "skipQR" {
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
    let titleString = NSLocalizedString("Connection error", comment: "")
    let bodyString = String(
      format: NSLocalizedString("Connection to the server in Wi-Fi network '%@' was lost.", comment: ""),
      remote.ssid
    )
    let tryAgainButtonString = NSLocalizedString("Reconnect", comment: "")

    let alert = UIAlertController(
      title: titleString,
      message: bodyString,
      preferredStyle: .alert
    )
    alert.addAction(UIAlertAction(title: tryAgainButtonString, style: .cancel, handler: { [weak self] (action) in
      self?.start()
    }))
    present(alert, animated: true, completion: nil)
  }

  func start () {
    if (rs.connection.isConnected) {
      rs.connection.disconnect()
    }
    
    if isSimulationEnv() {
      skipQR()
      return
    }
    getScanner()?.startScanner()
  }

  private func jumpToInspiration () {
    performSegue(withIdentifier: "skipQR", sender: nil)
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
