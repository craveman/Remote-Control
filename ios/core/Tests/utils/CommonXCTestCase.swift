
import XCTest

import logging
import networking


open class AbstractTestCase: XCTestCase, Loggable {

  open override func setUp () {
    super.setUp()
    LogContext.ROOT.logLevel = .DEBUG
    NetworkManager.shared.close()
  }
}