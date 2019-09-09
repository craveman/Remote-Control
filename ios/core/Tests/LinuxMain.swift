import XCTest

import networkingTests


var tests = [XCTestCaseEntry]()
tests += SM02Tests.allTests()
tests += NetworkTests.allTests()
XCTMain(tests)
