
final class HandlersManager<Type> {

  typealias Handler = (_ it: Type) -> Void

  // private var handlers = ThreadedArray<Handler>()
  private var handlers = [Handler]()

  func add (handler: @escaping Handler) {
    handlers.append(handler)
  }

  @discardableResult
  func fire (it: Type) -> Bool {
//    handlers.async { collection in
//      for handler in collection {
//        handler(it)
//      }
//    }
    for handler in handlers {
      handler(it)
    }
    return !handlers.isEmpty
  }

  func clear () {
//    handlers.async { collection in
//      collection.removeAll()
//    }
    handlers.removeAll()
  }
}
