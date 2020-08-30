//
//  LanConfigReader.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation

class LanConfigReader: ObservableObject {
  @Published var config: LanConfig? = nil
  @Published var hasError: Bool? = nil
  private let udpReader = Sm02UdpLookup()
  private var lookupTimer: Timer? = nil
  private let checksPerSec = 1;
  init() {
  }
  
  func startReader() -> Void {
    print("LanConfigReader::startReader")
    hasError = nil;
    if lookupTimer != nil {
      lookupTimer?.invalidate()
    }
    rs.lookup.start()
    lookupTimer = Timer.scheduledTimer(withTimeInterval: TimeInterval(1 / checksPerSec), repeats: true) {timer in
//      print("LanConfigReader::lookupTimer body")
      self.Sm02ConnectionConfigCheck()
    }
  }
  
  func stopReader() -> Void {
    if lookupTimer != nil {
         lookupTimer?.invalidate()
       }
    rs.lookup.stop()
  }
  
  private func Sm02ConnectionConfigCheck() {
    print(rs.lookup.remoteServers)
    guard let first = rs.lookup.remoteServers.first else {
      if (config != nil) {
        config = nil
      }
      
//      if (Int.random(in: 0..<100) > 95) {
//        config = LanConfig(ip: "192.168.0.101", code: [0,0,0,0,0])
//      }
      
      return
    }
    
    config = LanConfig(ip: first.ip, code: first.code)
  }
}

struct LanConfig {
  public let ip: String
  public let code: [UInt8]
}
