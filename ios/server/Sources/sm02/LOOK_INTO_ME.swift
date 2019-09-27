
import Sm02Client


func messageHandler (message: Outbound) -> Inbound? {
  print("received: \(message)")
  return nil
}

func eventHandler (event: ConnectionEvent) -> Void {
  print("event: \(event)")
}