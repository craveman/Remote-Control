
import utils


final class EventsManager {

  typealias EventHandler = (ConnectionEvent) -> Void

  private var handlers = ThreadedArray<EventHandler>()

  func add (handler: @escaping EventHandler) {
    handlers.append(handler)
  }

  func fire (it: ConnectionEvent) {
    handlers.async { collection in
      for handler in collection {
        handler(it)
      }
    }
  }

  func clear () {
    handlers.async { collection in
      collection.removeAll()
    }
  }
}
