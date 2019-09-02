
import utils


final class MessagesManager {

  typealias ResponseContainer = (response: Outbound?, processed: Bool)

  private var atomic = Atomic<InboundHandler?>(nil)

  func set (handler: @escaping InboundHandler) {
    atomic.store(handler)
  }

  func fire (it: Inbound) -> ResponseContainer {
    guard let handle = atomic.load() else {
      return (nil, false)
    }
    let response = handle(it)
    return (response, true)
  }

  func clear () {
    atomic.store(nil)
  }
}