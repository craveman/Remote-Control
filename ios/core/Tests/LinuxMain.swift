import XCTest

import RemoteControl_NetworkTests

var tests = [XCTestCaseEntry]()
tests += NetworkTests.allTests()
XCTMain(tests)
