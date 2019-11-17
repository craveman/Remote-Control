//
//  InMemoryState.swift
//  RemoteControl
//
//  Created by Artem Labazin on 27.10.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import struct Sm02Client.RemoteServer
import class Sm02Client.Atomic

class InMemoryState {

  static let shared = InMemoryState()

  private var innerRemoteServer: Atomic<RemoteServer?>? = nil;

  // var connected: Bool {
  //   return
  // }

  var remoteServer: RemoteServer? {
    set {
        innerRemoteServer = Atomic<RemoteServer?>(newValue)
        innerRemoteServer?.store(newValue)
    }
    get {
        return innerRemoteServer != nil ? innerRemoteServer?.load() : nil
    }
  }

  private init () {
    // noop
  }
}
