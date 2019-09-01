
import Dispatch


final class EventService {

  static let shared = EventService()

  let queue: DispatchQueue

  var handlers: [EventHandler]

  private init () {
    queue = DispatchQueue(
      label: "com.xxlabaza.events.service",
      qos: .background
    )
    handlers = []
  }

  func add (handler: @escaping EventHandler) {
    queue.sync {
      self.handlers.append(handler)
    }
  }

  func fire (event: ConnectionEvent) {
    queue.async(flags: .barrier) { [weak self] in
      guard let self = self else {
        return
      }
      for handler in self.handlers {
        handler(event)
      }
    }
  }

  func clear () {
    queue.sync {
      self.handlers.removeAll()
    }
  }
}
