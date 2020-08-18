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
  private let udpReader = Sm02UdpLookup()
  init() {
    
  }
}

class LanConfig {
  public let ip: String = ""
  public let code: [UInt8] = [0,0,0,0]
}
