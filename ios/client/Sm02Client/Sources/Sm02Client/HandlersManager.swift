
import struct Foundation.UUID
import class NIOConcurrencyHelpers.Lock

final class HandlersManager<Type> {

  typealias Handler = (_ it: Type) -> Void

  private let lock = Lock()
  private var handlers = [(key: UUID, handler: Handler)]()

  @discardableResult
  func add (handler: @escaping Handler) -> UUID {
    let key = UUID()
    let tuple = (key, handler)
    lock.withLockVoid {
      handlers.append(tuple)
    }
    return key
  }

  @discardableResult
  func remove (handler uuid: UUID) -> Bool {
    return lock.withLock {
      let optional = handlers.firstIndex(where: { key, handler in key == uuid })
      if let index = optional {
        handlers.remove(at: index)
        return true
      }
      return false
    }
  }

  @discardableResult
  func fire (it: Type) -> Bool {
    if handlers.isEmpty {
      return false
    }

    let handlersCopy = lock.withLock {
      handlers.map { $0.handler }
    }
    for handler in handlersCopy {
      handler(it)
    }
    return true
  }

  func clear () {
    lock.withLockVoid {
      handlers.removeAll()
    }
  }
}
