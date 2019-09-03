
import NIO
import NIOExtras
import XCTest

import sm02
import test_utils

@testable import networking


final class PingCatcherServiceTests: AbstractTestCase {

  static var allTests = [
    ("testCatchServerPing", testCatchServerPing),
  ]

  func testCatchServerPing () {
    var expect = expectation(description: "Catch ping message")

    let container = DependencyContainer()
    let catcher = PingCatcherService(factory: container)

    container.eventsManager.add(handler: { event in
      if case .pingCatched(_) = event {
        expect.fulfill()
      }
    })

    catcher.start()
    defer {
      catcher.stop()
    }

    let server = SM02().start()
    defer {
      server.stop()
    }
    waitForExpectations(timeout: 3)
  }
}
