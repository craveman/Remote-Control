//
//  ConnectionProcessor.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 14.04.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import UIKit
import NetworkExtension
import SystemConfiguration.CaptiveNetwork


fileprivate func log(_ items: Any...) {
  print("ConnectionProcessor:log: ", items)
}

struct ConnectionProcessor {
  public static let RECONNECT = "Try again"
  public static let ERROR = "Connection error"
  //     todo: ask user for location -> WiFi list reading
  private var hasWiFiReadingPermition = true
  
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
    let titleString = NSLocalizedString(ConnectionProcessor.ERROR, comment: "")
    let bodyString = String(
      format: NSLocalizedString(ConnectionProcessor.CONNECTION_FAILED, comment: ""),
      remote.ip, text
    )
    let tryAgainButtonString = NSLocalizedString(ConnectionProcessor.RECONNECT, comment: "")
    
    let alert = UIAlertController(
      title: titleString,
      message: bodyString,
      preferredStyle: .alert
    )
    alert.addAction(UIAlertAction(title: tryAgainButtonString, style: .cancel, handler: { action in
      self.controller.reader.startScanning()
    }))
    self.controller.showAlert(alert)
  }
}
