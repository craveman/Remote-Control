//
//  InMemoryState.swift
//  RemoteControl
//
//  Created by Artem Labazin on 27.10.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import struct Sm02Client.RemoteAddress
import class Sm02Client.Atomic

class InMemoryState {

  static let shared = InMemoryState()

  private var innerRemoteAddress: Atomic<RemoteAddress?>? = nil;

  // var connected: Bool {
  //   return
  // }

  var remoteServer: RemoteAddress? {
    set {
        innerRemoteAddress = Atomic<RemoteAddress?>(newValue)
        innerRemoteAddress?.store(newValue)
    }
    get {
        return innerRemoteAddress != nil ? innerRemoteAddress?.load() : nil
    }
  }

  private init () {
    // noop
  }
}
