
import Dispatch
import Foundation


class Scheduler {

  static let shared = Scheduler()

  let queue: DispatchQueue
  var timers: [String: DispatchSourceTimer]

  private init () {
    queue = DispatchQueue(label: "scheduledTasks", qos: .background)
    timers = [:]
  }

  deinit {
    stop()
  }

  func schedule (every repeating: DispatchTimeInterval, run action: @escaping () -> Void) -> String {
    let timer = DispatchSource.makeTimerSource(queue: queue)
    timer.schedule(deadline: .now(), repeating: repeating)
    timer.setEventHandler { action() }
    timer.resume()

    let id = UUID().uuidString
    queue.sync {
      timers[id] = timer
    }
    return id
  }

  func cancel (id: String) {
    if let timer = self[id] {
      timer.cancel()
    }
  }

  func stop () {
    queue.sync {
      var iterator = timers.makeIterator()
      while let (key, value) = iterator.next() {
        value.cancel()
        timers.removeValue(forKey: key)
      }
    }
  }

  subscript(id: String) -> DispatchSourceTimer? {
    var result: DispatchSourceTimer?
    queue.sync {
      result = timers[id]
    }
    return result
  }
}
