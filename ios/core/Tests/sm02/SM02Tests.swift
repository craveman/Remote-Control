
import Foundation
import Network
import Dispatch
import XCTest

@testable import sm02
import logging
import networking


final class SM02Tests: XCTestCase, Loggable {

  func testExample () {
    let server = SM02()
        .on(messages: handle)
        .start()

    defer {
      server.stop()
    }

    let expect = expectation(description: "Get TOCK-message back")
    var tockMessage: [UInt8]?
    sendTick({
        tockMessage = $0
        expect.fulfill()
    })

    waitForExpectations(timeout: 3)
    XCTAssertEqual(tockMessage, [0x00, 0x02, 0xF2, 0x01])
  }

  private func handle (request: Outbound) -> Inbound? {
    log.info("parsed request - {}", request)
    switch request {
    case .tick:
      return .tock
    default:
      return nil
    }
  }

  private func sendTick (_ handler: @escaping ([UInt8]) -> Void) {
    let tockBytes: [UInt8] = [0x00, 0x02, 0xF1, 0x00]
    var data = Data(capacity: tockBytes.count)
    data.append(contentsOf: tockBytes)

    let endpointPort = NWEndpoint.Port(rawValue: UInt16(21074))!
    let endpointHost = NWEndpoint.Host("127.0.0.1")
    let connection = NWConnection(host: endpointHost, port: endpointPort, using: .tcp)


    connection.send(content: data, completion: .contentProcessed({ error in
        self.log.debug("message sent")
        if let exception = error {
          self.log.error("error occured - {}", exception)
        }
    }))
    connection.receive(minimumIncompleteLength: 1, maximumLength: 65536) { (data, _, isComplete, error) in
        defer {
          connection.cancel()
        }

        if let exception = error {
          self.log.error("something goes wrong {}", exception)
        } else if let data = data, !data.isEmpty {
          let bytes = [UInt8](data)
          self.log.info("received response {}", bytes)
          handler(bytes)
        }
    }

    connection.start(queue: DispatchQueue.global(qos: .background))
  }

  static var allTests = [
    ("testExample", testExample),
  ]
}
