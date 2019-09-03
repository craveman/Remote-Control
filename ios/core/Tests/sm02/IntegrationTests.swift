
import Foundation
import Network
import Dispatch
import XCTest

import logging
import test_utils

@testable import sm02
@testable import networking


final class IntegrationTests: AbstractTestCase {

  static var allTests = [
    ("testComplex", testComplex),
  ]

  func testComplex () {
    let server = SM02()
        .handle(messages: { request in
          switch request {
          case .setName(_, _):
            return .genericResponse(request: request.tag)
          default:
            return nil
          }
        })
        .start()

    defer {
      server.close()
    }

    let expectConnection = XCTestExpectation(description: "Get connection")
    let expectResponse = XCTestExpectation(description: "Get response")

    let container = DependencyContainer()
    let client = NetworkManager(factory: container)

    client.handle(messages: { inbound in
          switch inbound {
          case let .genericResponse(request) where request == 0x01: // ответ на setName
            expectResponse.fulfill()
            print("response catched")
          default:
            print("unsupported inbound message \(inbound)")
          }
        })
        .on(events: { event in
          if case .pingCatched(_) = event {
            print("ping catched")
            expectConnection.fulfill()

            let request = Outbound.setName(person: .right, name: "Artem")
            client.send(message: request)
          }
        })
        .start()

    defer {
      client.close()
    }

    wait(for: [expectConnection, expectResponse], timeout: 10.0)
  }

  private func handle (request: Outbound) -> Inbound? {
    log.info("parsed request - {}", request)
    switch request {
    case .disconnect:
      return .genericResponse(request: request.tag)
    default:
      return nil
    }
  }
}
