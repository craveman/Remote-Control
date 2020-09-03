//
//  ConnectionsViewController.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 10/09/2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import UIKit

fileprivate func log(_ items: Any...) {
  print("ConnectionsViewController:log: ", items)
}

class ConnectionsViewController: UIViewController, UIAdaptivePresentationControllerDelegate {
  public static let RECONNECT = "Reconnect"
  public static let CONNECTION_LOST = "Connection to the server was lost."
  public static let CONNECTION_ERROR = "Connection error"
  var alert: UIAlertController?
  
  let segueName = "jumpToInspiration"
  @IBOutlet weak var connectionConfigReaderSubViewWrapper: UIView!
  
  override func viewDidAppear (_ animated: Bool) {
    log("did appear")
    getScanner()?.onSuccess({ [weak self] in
      log("scanner on success", self == self)
      guard self == self else {
        return
      }
      self?.jumpToInspiration()
    })

    if !isSimulationEnv() {
      start()
    }
    
    super.viewDidAppear(animated)
  }

  private func getScanner () -> ConnectionControllerProtocol? {
    guard connectionConfigReaderSubViewWrapper.subviews.count > 0 else {
      return nil
    }
    return connectionConfigReaderSubViewWrapper.subviews[0].next as? ConnectionControllerProtocol
  }

  override func prepare (for segue: UIStoryboardSegue, sender: Any?) {
    guard connectionConfigReaderSubViewWrapper.subviews.count > 0 else {
      return
    }
    if segue.identifier == segueName {
      segue.destination.presentationController?.delegate = (self as UIAdaptivePresentationControllerDelegate)
    }
    if isSimulationEnv() {
      return
    }
    if let reader = connectionConfigReaderSubViewWrapper.subviews[0].next as? ConnectionControllerProtocol {
      reader.stopScanner()
    }
  }

  func presentationControllerDidDismiss (_ presentationController: UIPresentationController) {
    start(true)
  }

  func warning (_ remote: RemoteAddress) {
    let bodyString = String(
      format: NSLocalizedString(ConnectionsViewController.CONNECTION_LOST, comment: "")
    )
    prepareAlert(bodyString)
    present(self.alert!, animated: true, completion: nil)
  }

  func start (_ disconnect: Bool = false) {
    if (disconnect && rs.connection.isConnected) {
      log("startScanner with disconnect")
      rs.connection.disconnect()
    }
    
    getScanner()?.startScanner()
  }
  
  func stop () {
    getScanner()?.stopScanner()
    self.alert?.dismiss(animated: false, completion: {
      log(":stop alert::dismissed")
    })
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
    stop()

    performSegue(withIdentifier: segueName, sender: nil)
  }

  private func isSimulationEnv () -> Bool {
    #if targetEnvironment(simulator)
      return true
    #else
      return false
    #endif
  }

}
