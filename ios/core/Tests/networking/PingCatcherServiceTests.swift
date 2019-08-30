
import NIO
import NIOExtras
import XCTest

import sm02

@testable import networking


final class PingCatcherServiceTests: XCTestCase {

  func testCatchServerPing () {
    var expect = expectation(description: "Catch ping message")

    var catchResult: SocketAddress?
    let catcher = PingCatcherService { remoteAddress in
      catchResult = remoteAddress
      expect.fulfill()
    }

    catcher.start()
    defer {
      catcher.stop()
    }

    let server = SM02().start()
    defer {
      server.stop()
    }
    waitForExpectations(timeout: 3)
    XCTAssertNotNil(catchResult)
  }

  static var allTests = [
    ("testCatchServerPing", testCatchServerPing),
  ]
}
