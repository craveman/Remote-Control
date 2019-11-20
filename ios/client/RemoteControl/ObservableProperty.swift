//
//  Observers.swift
//  RemoteControl
//
//  Created by Artem Labazin on 20.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import Foundation
import NIOConcurrencyHelpers

typealias Observer<T> = (_ update: T) -> Void

class ObserversManager<T> {

  fileprivate let observersLock = Lock()
  fileprivate var observers: [(uuid:UUID, value:Observer<T>)] = []

  @discardableResult
  func on (change observer: @escaping Observer<T>) -> UUID {
    let key = UUID()
    let tuple = (key, observer)
    observersLock.withLockVoid {
      observers.append(tuple)
    }
    return key
  }

  @discardableResult
  func remove (observer uuid: UUID) -> Bool {
    return observersLock.withLock {
      let optional = observers.firstIndex(where: { key, value in key == uuid})
      if let index = optional {
        observers.remove(at: index)
        return true
      } else {
        return false
      }
    }
  }
}

final class FirableObserversManager<T>: ObserversManager<T> {

  func fire (with value: T) {
    if observers.isEmpty {
      return
    }

    let observersCopy = observersLock.withLock {
      observers.map { $0.value }
    }

    for observer in observersCopy {
      observer(value)
    }
  }
}

class ObservableProperty<T> {
  
  fileprivate let observersManager = FirableObserversManager<T>()
  
  @discardableResult
  func on (change observer: @escaping Observer<T>) -> UUID {
    return observersManager.on(change: observer)
  }
  
  @discardableResult
  func remove (observer uuid: UUID) -> Bool {
    return observersManager.remove(observer: uuid)
  }
}

final class PrimitiveProperty<T: AtomicPrimitive>: ObservableProperty<T> {

  private let atomic: Atomic<T>

  init (_ initial: T) {
    atomic = Atomic(value: initial)
  }
  
  func set (_ value: T) {
    atomic.store(value)
    observersManager.fire(with: value)
  }

  func get () -> T {
    return atomic.load()
  }
}

final class ObjectProperty<T: AnyObject>: ObservableProperty<T> {

  private let atomic: AtomicBox<T>

  init (_ initial: T) {
    atomic = AtomicBox(value: initial)
  }
  
  func set (_ value: T) {
    atomic.store(value)
    observersManager.fire(with: value)
  }

  func get () -> T {
    return atomic.load()
  }
}
