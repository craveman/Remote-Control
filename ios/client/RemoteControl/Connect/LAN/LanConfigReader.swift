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
  @Published var options: [(address: RemoteAddress, busy: Bool)] = []
  @Published var hasError: Bool? = nil
  private var lookupTimer: Timer? = nil
  private let checksPerSec = 0.25;
  init() {
  }

  func startReader() -> Void {
    print("LanConfigReader::startReader")
    hasError = nil;
    if lookupTimer != nil {
      lookupTimer?.invalidate()
    }
    if (!rs.lookup.isStarted) {
      rs.lookup.start()
    }
    
    lookupTimer = Timer.scheduledTimer(withTimeInterval: TimeInterval(1 / checksPerSec), repeats: true) {timer in
      self.Sm02ConnectionConfigCheck()
    }
    withDelay({
      self.Sm02ConnectionConfigCheck()
    }, 0.5)
  }

  func stopReader() -> Void {
    if lookupTimer != nil {
         lookupTimer?.invalidate()
       }
    if (rs.lookup.isStarted) {
      rs.lookup.stop()
    }
    
  }

  private func Sm02ConnectionConfigCheck() {
    let knownIps = options.map({ $0.address.ip })
    let newOptionsList = rs.lookup.remoteAddresses.filter({ !knownIps.contains($0.address.ip) })
   
    options.removeAll(where: { old in
      return !rs.lookup.remoteAddresses.contains(where: {
        if $0.address.ip == old.address.ip && $0.busy == old.busy {
          return true;
        }
        print("Options removing old: ", old)
        return false
      })
    })
    
    if newOptionsList.count > 0 {
      options.append(contentsOf: newOptionsList)
      print("added options: ", newOptionsList)
    }
    
    guard let option = rs.lookup.remoteAddresses.last else {
      if (config != nil) {
        config = nil
      }

      return
    }
    
    config = LanConfig(ip: option.address.ip, code: option.address.code)
  }
}

struct LanConfig {
  public let ip: String
  public let code: [UInt8]
}
