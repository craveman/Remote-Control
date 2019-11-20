
public class Atomic2<Element> : ThreadedObject<Element> {

  /// Initializes this atomic element from a normal element.
  ///
  /// - parameters:
  ///     - value: Value to store.
  ///
  ///     - type: The access type for this attomic element.
  public override init (_ value: Element, type: ThreadingType = .concurrent) {
    super.init(value, type: type)
  }

  /// Performs a synchronous access to the internal value and returns its
  /// state.
  ///
  /// - returns: The value stored in the atomic object.
  ///
  /// _Synchronous Method_
  public func load () -> Element {
    return sync { value in
      return value
    }
  }

  /// Performs an asynchronous action on the internal value.
  ///
  /// - parameters:
  ///     - action: A closure containing code that which may access the
  ///               internal value of this atomic object.
  ///
  /// _Asynchronous Method_
  public func loading (_ action: @escaping (inout Element) -> Void) {
    async { value in
      action(&value)
    }
  }

  /// Stores a given value in this atomic object.
  ///
  /// - parameters:
  ///     - newValue: The value to store.
  ///
  /// _Asynchronous Method_
  public func store (_ newValue: Element) {
    async { value in
      value = newValue
    }
  }
}

extension Atomic2: CustomStringConvertible
    where Element: CustomStringConvertible {

  public var description: String {
    return load().description
  }
}

extension Atomic2: CustomDebugStringConvertible
    where Element: CustomDebugStringConvertible {

  public var debugDescription: String {
    return load().debugDescription
  }
}
