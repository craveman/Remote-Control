//
//  LanAccessHelper.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 05.10.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation
fileprivate func log(_ items: Any...) {
//  print("LanAccessHelper:log: ", items)
}
fileprivate func udpSend(textToSend: String, address addr: sockaddr_in) {
  let host = ProcessInfo.processInfo.hostName
  let fd = socket(AF_INET, SOCK_DGRAM, 0) // DGRAM makes it UDP
  
  let sent = textToSend.withCString { cstr -> Int in
    
    var localCopy = addr
    
    let sent = withUnsafePointer(to: &localCopy) { pointer -> Int in
      let memory = UnsafeRawPointer(pointer).bindMemory(to: sockaddr.self, capacity: 1)
      let sent = sendto(fd, cstr, strlen(cstr), 0, memory, socklen_t(addr.sin_len))
      return sent
    }
    
    return sent
  }
  log("\(host) udpSend to \(addr.sin_addr) count: \(sent)")
  close(fd)
  
}

func checkLanPermission(ip: String = "192.168.31.252", _ port: UInt16 = 21075) -> Void {
  let word = "HELLO:(SM_RC@IOS)"
  var addr = sockaddr_in()
  addr.sin_len = UInt8(MemoryLayout.size(ofValue: addr))
  addr.sin_family = sa_family_t(AF_INET)
  addr.sin_port = port
  addr.sin_addr.s_addr = inet_addr(ip)
  udpSend(textToSend: word, address: addr)
}
