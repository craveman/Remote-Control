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

    guard let option = rs.lookup.remoteAddresses.last else {
      if (config != nil) {
        config = nil
      }

      return
    }
    config = LanConfig(ip: option.ip, code: option.code)
  }
}

struct LanConfig {
  public let ip: String
  public let code: [UInt8]
}
