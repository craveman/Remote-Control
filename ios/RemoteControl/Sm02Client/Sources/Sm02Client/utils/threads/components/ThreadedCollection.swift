
public protocol ThreadedCollection {

  associatedtype InternalCollectionType

  /// Returns an unmanaged version of the underlying object.
  var unthreaded: InternalCollectionType { get }
}
