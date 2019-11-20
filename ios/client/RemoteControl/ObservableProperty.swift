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

protocol Property {
  
  associatedtype PropertyType
  
  func get () -> PropertyType
}

protocol ObservableProperty: Property {
  
  associatedtype PropertyType
  
  mutating func on (change observer: @escaping Observer<PropertyType>) -> UUID
  mutating func remove (observer key: UUID)
}

class CommonObservableProperty<T> {
  
  fileprivate typealias SetData = (_ value: T) -> Void
  
  private let setData: SetData
  private let observersLock = Lock()
  private var observers: [(uuid:UUID, value:Observer<T>)] = []
  
  fileprivate init (setData: @escaping SetData) {
    self.setData = setData
  }
  
  func set (_ value: T) {
    setData(value)
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
  
  func on (change observer: @escaping Observer<T>) -> UUID {
    let key = UUID()
    let tuple = (key, observer)
    observersLock.withLockVoid {
      observers.append(tuple)
    }
    return key
  }
  
  func remove (observer key: UUID) {
    observersLock.withLockVoid {
      let optional = observers.firstIndex(where: { uuid, value in uuid == key})
      if let index = optional {
        observers.remove(at: index)
      }
    }
  }
}

final class PrimitiveProperty<T: AtomicPrimitive>: CommonObservableProperty<T>, ObservableProperty {
  
  private let atomic: Atomic<T>
  
  init (_ initial: T) {
    atomic = Atomic(value: initial)
    super.init(setData: atomic.store)
  }
  
  func get () -> T {
    return atomic.load()
  }
}

final class ObjectProperty<T: AnyObject>: CommonObservableProperty<T>, ObservableProperty {
  
  private let atomic: AtomicBox<T>

  init (_ initial: T) {
    atomic = AtomicBox(value: initial)
    super.init(setData: atomic.store)
  }
  
  func get () -> T {
    return atomic.load()
  }
}
