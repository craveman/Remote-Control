//
//  NetworkReachability.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 14.04.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
import Network
//import UIKit

fileprivate func log(_ items: Any...) {
  print("NetworkReachability:log: ", items)
}

class NetworkReachability {
  
  var pathMonitor: NWPathMonitor!
  var path: NWPath?
  lazy var pathUpdateHandler: ((NWPath) -> Void) = {[weak self] path in
    log(path)
    self?.path = path
    if path.status == NWPath.Status.satisfied {
      log("Connected")
    } else if path.status == NWPath.Status.unsatisfied {
      log("unsatisfied")
//      DispatchQueue.main.async {
//        let appDelegate = UIApplication.shared.delegate as! AppDelegate
//        appDelegate.disconnectedByConnection()
//      }
      
    } else if path.status == NWPath.Status.requiresConnection {
      log("requiresConnection")
    }
  }
  
  let backgroudQueue = DispatchQueue.global(qos: .background)
  
  init() {
    pathMonitor = NWPathMonitor()
    pathMonitor.pathUpdateHandler = self.pathUpdateHandler
    
  }
  
  func start() -> Void {
    pathMonitor.start(queue: backgroudQueue)
  }
  
  func stop() -> Void {
    pathMonitor.cancel()
  }
  
  func isNetworkAvailable() -> Bool {
    if let path = self.path {
      if path.status == NWPath.Status.satisfied {
        return true
      }
    }
    return false
  }
}

