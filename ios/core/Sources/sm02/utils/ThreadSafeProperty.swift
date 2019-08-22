
import Dispatch
import Foundation


public class ThreadSafeProperty<T> {

  private let queue = DispatchQueue(
      label: "changes\(T.self)Queue-\(UUID().uuidString)",
      attributes: .concurrent
  )
  private var _value: T?

  public var value: T? {
    set {
      queue.async(flags: .barrier) {
        self._value = newValue
      }
    }
    get {
      var result: T?
      queue.sync {
        result = _value
      }
      return result
    }
  }
}
