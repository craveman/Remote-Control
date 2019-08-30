
import Foundation


/// Linked list implementation of a first-in first-out queue. Insertion and
/// extraction operators are O(1). This collection does not provide random
/// access to its elements.
public struct LinkedQueue<T> {

  public typealias Element = T

  /// Number of elements in the queue.
  public fileprivate(set) var count: Int = 0

  fileprivate var root: LinkedNode? = nil
  fileprivate var tail: LinkedNode? = nil

  public init() { }

  /// Linked list node class.
  class LinkedNode {

    var element: Element
    var next: LinkedNode?

    init (_ element: Element, _ next: LinkedNode? = nil) {
      self.element = element
      self.next = next
    }
  }
}

// MARK: Computed Properties
public extension LinkedQueue {

  /// First element of the queue if any.
  var front: Element? {
    return root?.element
  }

  /// Last element of the queue if any.
  var back: Element? {
    return tail?.element
  }

  /// Returns `true` if there are no elements in the queue, `false` otherwise.
  var isEmpty: Bool {
    return count == 0
  }
}

// MARK: Mutating Methods
public extension LinkedQueue {

  /// Adds an item to the end of the queue.
  ///
  /// - parameters:
  ///     - newElement: The element to add to the queue.
  ///
  /// - complexity: _O(1)_
  mutating func enqueue (_ newElement: Element) {
    if let t = tail {
      t.next = LinkedNode(newElement)
      tail = t.next
    } else {
      root = LinkedNode(newElement)
      tail = root
    }
    count += 1
  }

  /// Removes and returns the first element of the queue.
  ///
  /// - complexity: _O(1)_
  ///
  /// - warning: Do not call this method on an empty queue; method will throw
  ///     a fatal error.
  @discardableResult
  mutating func dequeue () -> Element {
    if root == nil {
      fatalError("dequeue called on empty queue")
    }
    let element = root!.element

    root = root?.next
    count -= 1
    if count == 0 {
      tail = nil
    }
    return element
  }
}

// MARK: Non-mutating Methods
public extension LinkedQueue {

  /// Performs an action for each element in the queue.
  ///
  /// - parameters:
  ///     - callback: The function to call on every element.
  ///
  /// - complexity: _O(n * x)_ where n is the size of the queue and x is the
  ///     complexity of `callback`
  func forEach (_ callback: @escaping (Element) throws -> Void) rethrows {
    var node = root
    while node != nil {
      try callback(node!.element)
      node = node!.next
    }
  }

  /// Returns an array whose elements are the result of a transformation done
  /// to each element of the queue.
  ///
  /// - parameters:
  ///     - transform: A function called on each element in the queue to
  ///         change the element into the desired type.
  func map<U> (_ transform: @escaping (Element) throws -> U) rethrows -> [U] {
    var result = [U]()
    try forEach { element in
      let transformed = try transform(element)
      result.append(transformed)
    }
    return result
  }
}