
import Foundation


/// Base class for all threaded objects. Provides asyncronous and syncronous
/// views for writting and reading respectively.
public class ThreadedObject<Element> {

  /// The type of dispatch queue used in this object; either concurrent or
  /// serial.
  public private(set) var threadingType: ThreadingType

  /// Underlying value to be accessed by the different views.
  var threadedValue: Element

  /// Dispatch queue to control interactions with this object.
  private var queue: DispatchQueue

  internal init (_ value: Element, type: ThreadingType) {
    threadedValue = value
    threadingType = type

    queue = threadingType == .concurrent
            ? DispatchQueue(label: LabelDispatch.get(), attributes: .concurrent)
            : DispatchQueue(label: LabelDispatch.get())
  }

  /// Asyncronous interaction with this object; used for writting to
  /// the underlying value.
  public func async (_ callback: @escaping (inout Element) -> Void) {
    switch (threadingType) {
    case .concurrent:
      queue.async(flags: .barrier) {
        callback(&self.threadedValue)
      }
    case .serial:
      queue.async {
        callback(&self.threadedValue)
      }
    }
  }

  /// Syncronous internaction with this object; used for reading from the
  /// underlying value.
  public func sync<ReturnType> (_ callback: @escaping (Element) -> ReturnType) -> ReturnType {
    return queue.sync {
      return callback(self.threadedValue)
    }
  }

  /// Mutable syncronous interaction with this object; used when needing to
  /// both read and write to the underlying value.
  public func mutatingSync<ReturnType> (_ callback: @escaping (inout Element) -> ReturnType) -> ReturnType {
    return queue.sync(flags: .barrier) {
      return callback(&self.threadedValue)
    }
  }
}