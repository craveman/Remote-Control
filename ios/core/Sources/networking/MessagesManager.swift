
import utils


final class MessagesManager {

  private var atomic = Atomic<InboundHandler?>(nil)

  func set (handler: @escaping InboundHandler) {
    atomic.store(handler)
  }

  func fire (it: Inbound) -> Bool {
    guard let handle = atomic.load() else {
      return false
    }
    handle(it)
    return true
  }

  func clear () {
    atomic.store(nil)
  }
}