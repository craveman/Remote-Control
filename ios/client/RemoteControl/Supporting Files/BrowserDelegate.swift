//
//  BrowserDelegate.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
import Combine

fileprivate func log(_ items: Any...) {
  print("`smBrowserDelegate:log: ", items)
}

enum SMNetBrowserAction {
  case serviceFound
  case serviceRemoved
}

class SMNetBrowserDelegate : NSObject, NetServiceBrowserDelegate {
  private var resolver: (NetService, SMNetBrowserAction) -> Void = { _, _ in }
  init(_ serviceResolver: @escaping (NetService, SMNetBrowserAction) -> Void) {
    self.resolver = serviceResolver
  }
  
  func netServiceBrowser(_ netServiceBrowser: NetServiceBrowser,
                         didFindDomain domainName: String,
                         moreComing moreDomainsComing: Bool) {
    log("netServiceDidFindDomain", domainName)
  }
  
  func netServiceBrowser(_ netServiceBrowser: NetServiceBrowser,
                         didRemoveDomain domainName: String,
                         moreComing moreDomainsComing: Bool) {
    log("netServiceDidRemoveDomain", domainName)
  }
  
  func netServiceBrowser(_ netServiceBrowser: NetServiceBrowser,
                         didFind netService: NetService,
                         moreComing moreServicesComing: Bool) {
    netService.resolve(withTimeout: 0)
    withDelay({
      self.resolver(netService, .serviceFound)
    }, 0.1)
    log("netServiceDidFindService", netService)
  }
  
  
  func netServiceBrowser(_ netServiceBrowser: NetServiceBrowser,
                         didRemove netService: NetService,
                         moreComing moreServicesComing: Bool) {
    log("netServiceDidRemoveService", netService)
    self.resolver(netService, .serviceRemoved)
  }
  
  func netServiceBrowserWillSearch(_ netServiceBrowser: NetServiceBrowser){
    log("netServiceBrowserWillSearch")
  }
  
  func netServiceBrowser(_ browser: NetServiceBrowser, didNotSearch errorDict: [String : NSNumber]) {
    log("netServiceDidNotSearch", errorDict)
  }
  
  func netServiceBrowserDidStopSearch(_ netServiceBrowser: NetServiceBrowser) {
    log("netServiceDidStopSearch")
  }
  
}
