
import Sm02Client


typealias MessagesProcessor = (_ message: Outbound) -> Inbound?
typealias EventsProcessor = (_ event: ConnectionEvent) -> Void
