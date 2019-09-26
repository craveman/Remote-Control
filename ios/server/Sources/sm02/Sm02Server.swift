
import Sm02Client


typealias MessagesProcessor = (_ message: Outbound) -> Inbound?
typealias EventsProcessor = (_ event: ConnectionEvent) -> Void


final class Sm02Server {

  let code: [Int]
  let onMessage: MessagesProcessor
  let onEvent: EventsProcessor

  init (code: [Int],
        onMessage: @escaping MessagesProcessor,
        onEvent: @escaping EventsProcessor
  ) {
    self.code = code
    self.onMessage = onMessage
    self.onEvent = onEvent
  }

  func start () {
    print("Server started")
  }
}