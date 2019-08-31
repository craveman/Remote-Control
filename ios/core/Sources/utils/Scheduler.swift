
import Dispatch
import Foundation


public class Scheduler {

  public static let shared = Scheduler()

  let queue: DispatchQueue
  var timers: [UUID: DispatchSourceTimer]

  private init () {
    queue = DispatchQueue(label: "scheduledTasks", qos: .background)
    timers = [:]
  }

  deinit {
    stop()
  }

  public func schedule (every repeating: DispatchTimeInterval, run action: @escaping () -> Void) -> UUID {
    let timer = DispatchSource.makeTimerSource(queue: queue)
    timer.schedule(deadline: .now(), repeating: repeating)
    timer.setEventHandler { action() }
    timer.resume()

    let id = UUID()
    queue.sync {
      timers[id] = timer
    }
    return id
  }

  public func cancel (id: UUID) {
    if let timer = self[id] {
      timer.cancel()
    }
  }

  public func stop () {
    queue.sync {
      var iterator = timers.makeIterator()
      while let (key, value) = iterator.next() {
        value.cancel()
        timers.removeValue(forKey: key)
      }
    }
  }

  public subscript(id: UUID) -> DispatchSourceTimer? {
    var result: DispatchSourceTimer?
    queue.sync {
      result = timers[id]
    }
    return result
  }
}
