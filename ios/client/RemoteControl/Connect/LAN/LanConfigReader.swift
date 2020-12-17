//
//  LanConfigReader.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 16.08.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import Foundation

typealias LanConfigReaderOption = (address: RemoteAddress, busy: Bool, name: String)

fileprivate let mockedConnectionList: [LanConfigReaderOption] = [
  (address: RemoteAddress.empty, busy: true, name: "2"),
  (address: RemoteAddress.empty, busy: false, name: "3"),
  (address: RemoteAddress.empty, busy: true, name: "5"),
  (address: RemoteAddress.empty, busy: true, name: "8"),
  
  (address: RemoteAddress.empty, busy: false, name: "21"),
  (address: RemoteAddress.empty, busy: false, name: "ABC"),
  (address: RemoteAddress.empty, busy: true, name: "XYZ"),
//  (address: RemoteAddress.empty, busy: false, name: "55"),
  (address: RemoteAddress.empty, busy: false, name: "FINAL"),
  (address: RemoteAddress.empty, busy: true, name: "13"),
  (address: RemoteAddress.empty, busy: false, name: "34"),
]

fileprivate func primarySort(lhs: LanConfigReaderOption, rhs: LanConfigReaderOption) -> Bool {
  // digital asc
  //  titles asc
  let lNum = Int(lhs.name)
  let rNum = Int(rhs.name)
  if lNum == nil {
    if rNum == nil {
      return lhs.address.ip < rhs.address.ip
    }
    return false
  }
  
  if rNum == nil {
    return true
  }
  
  return lNum! < rNum!
            
}

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
    // todo: check option if needed
//    checkLanPermission(ip: option.address.ip)
  }

  private func Sm02ConnectionConfigCheck() {
    var list = rs.lookup.remoteAddresses
//    var list = mockedConnectionList // rs.lookup.remoteAddresses
    list.sort(by: primarySort)
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
