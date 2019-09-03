
import Foundation
import Network
import Dispatch
import XCTest

import logging
import networking
import test_utils

@testable import sm02


final class SM02Tests: AbstractTestCase {

  static var allTests = [
    ("testExample", testExample),
  ]

  func testExample () {
    let server = SM02()
        .handle(messages: handler)
        .start()

    defer {
      server.close()
    }

    let expect = expectation(description: "Get response back")
    var response: [UInt8]?
    sendDisconnect({
        response = $0
        expect.fulfill()
    })

    waitForExpectations(timeout: 3)
    XCTAssertEqual(response, [0x00, 0x03, 0xAA, 0x01, 0x0F])
  }

  private func handler (request: Outbound) -> Inbound? {
    log.info("parsed request - {}", request)
    switch request {
    case .disconnect:
      return .genericResponse(request: request.tag)
    default:
      return nil
    }
  }

  private func sendDisconnect (_ handler: @escaping ([UInt8]) -> Void) {
    let disconnectBytes: [UInt8] = [0x00, 0x02, 0x0F, 0x00]
    var data = Data(capacity: disconnectBytes.count)
    data.append(contentsOf: disconnectBytes)

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
}
