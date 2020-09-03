//
//  ConnectionControllerProtocol.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
import UIKit

protocol ConnectionControllerProtocol: UIViewController {
  var isOnWiFiLookup: Bool { get set }
  var alert: UIAlertController? { get set }
  func showAlert(_ alert: UIAlertController) -> Void
  func onSuccess(_ action: @escaping () -> Void) -> Void
  func doConnectionCompletion () -> Void
  func doConnectionRejection () -> Void
  func startScanner () -> Void
  func stopScanner () -> Void
}


