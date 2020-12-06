//
//  LanConfigReader.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation

typealias LanConfigReaderOption = (address: RemoteAddress, busy: Bool, name: String)

class LanConfigReader: ObservableObject {
  @Published var primaryConfig: LanConfig? = nil
  @Published var options: [LanConfigReaderOption] = []
  @Published var hasError: Bool? = nil
  private var lookupTimer: Timer? = nil
  private let checksPerSec = 0.25;
  init() {
  }

  func startReader() -> Void {
    print("LanConfigReader::startReader")
    hasError = nil
    primaryConfig = nil
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
  
  private func checkOption(_ option: LanConfigReaderOption) {
    checkLanPermission(ip: option.address.ip)
  }

  private func Sm02ConnectionConfigCheck() {
    var list = rs.lookup.remoteAddresses
    list.sort(by: {lhs, rhs in return lhs.name < rhs.name})
    options.removeAll(where: { _ in true })
    options.append(contentsOf: list)
    
    options.forEach({ opt in self.checkOption(opt) })
   
//    print("result options list: ", options)
    
    guard let option = list.first(where: {!$0.busy}) else {
      if (primaryConfig != nil) {
        primaryConfig = nil
      }

      return
    }
    if (primaryConfig?.ip == option.address.ip && primaryConfig?.code == option.address.code) {
      return
    }
    primaryConfig = LanConfig(ip: option.address.ip, port: option.address.port, code: option.address.code)
  }
}

struct LanConfig {
  public let ip: String
  public let port: UInt16
  public let code: [UInt8]
}
