//
//  NetworkReachability.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 14.04.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
import Network

fileprivate func log(_ items: Any...) {
  print("NetworkReachability:log: ", items)
}

class NetworkReachability {
  
  var pathMonitor: NWPathMonitor!
  var path: NWPath?
  lazy var pathUpdateHandler: ((NWPath) -> Void) = {[weak self] path in
    self?.path = path
    log(path)
  }
  
  let backgroudQueue = DispatchQueue.global(qos: .background)
  
  init() {
    pathMonitor = NWPathMonitor()
    pathMonitor.pathUpdateHandler = self.pathUpdateHandler
    
  }
  
  init(withHandler handler: ((NWPath) -> Void)?) {
    pathMonitor = NWPathMonitor()
    pathMonitor.pathUpdateHandler = {[weak self] path in
      self?.path = path
      handler?(path)
    }
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

