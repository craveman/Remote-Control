
import Foundation
import Dispatch
import Network

import logging
import utils


final class PingBroadcastService: Loggable {

  private static let ping = "PING".data(using: .utf8)!

  var scheduledJobId: UUID?
  var connection: NWConnection?

  init? (host: String = "127.0.0.1", port: Int = 21075) {
    guard let endpointPort = NWEndpoint.Port(rawValue: UInt16(port)) else {
      return nil
    }
    let endpointHost = NWEndpoint.Host(host)
    connection = NWConnection(host: endpointHost, port: endpointPort, using: .udp)
  }

  deinit {
    stop()
  }

  func start () {
    log.debug("starting...")
    connection?.start(queue: DispatchQueue.global(qos: .background))

    scheduledJobId = Scheduler.shared.schedule(every: .seconds(2), run: { [weak self] in
        guard let self = self else {
          return
        }

        self.connection?.send(content: PingBroadcastService.ping, completion: .contentProcessed({ error in
            self.log.debug("heartbeat complete")
            if let exception = error {
              self.log.error("broadcasting with {} has error - {}", PingBroadcastService.ping, exception)
            }
            self.log.debug("heartbeat no error")
        }))
    })
    log.debug("started")
  }

  func stop () {
    log.debug("stopping...")
    if let jobId = scheduledJobId {
      Scheduler.shared.cancel(id: jobId)
      log.debug("scheduled job '{}' canceled", jobId)
    }
    if connection != nil {
      connection!.cancel()
      connection = nil
    }
    log.debug("stopped")
  }
}
