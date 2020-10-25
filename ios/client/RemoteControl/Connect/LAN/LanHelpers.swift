//
//  LanHelpers.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 09.10.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
import Network

fileprivate func log(_ items: Any...) {
  print("LanHelper:log: ", items)
}

func isValidConnection(path: NWPath) -> Bool {
  let wifi = path.usesInterfaceType(.wifi);
  let wired = path.usesInterfaceType(.wiredEthernet)
  let supportsPermissions = wifi || wired
  log("LAN support \(supportsPermissions) : [wifi, wired] \([wifi, wired])")
  return supportsPermissions;
}

class LanManualConfiguration {
  static let MANUAL_CONNECTION = NSLocalizedString("lan_manual Manual connection", comment: "")
  static let SET_CONFIG = NSLocalizedString("lan_manual Enter connection config", comment: "")
  static let JOIN_BTN = NSLocalizedString("lan_manual Join", comment: "")
  static let CANCEL_BTN = NSLocalizedString("lan_manual Cancel", comment: "")
  static let IP_INPUT_PLACEHOLDER = NSLocalizedString("lan_manual Enter IP address", comment: "")
}

class LanConnectionFails {
  static let NOT_FOUND = NSLocalizedString("Failed to connect to device please check configuration", comment: "")
  static let NOT_VALID = NSLocalizedString("Not valid lan configuration", comment: "")
}
