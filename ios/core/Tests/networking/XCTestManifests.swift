
import XCTest

#if !canImport(ObjectiveC)
public func allTests() -> [XCTestCaseEntry] {
  return [
    testCase(NetworkTests.allTests),
    testCase(PingCatcherServiceTests.allTests),
    testCase(ReadTimeoutTests.allTests),
    testCase(TickTockHandlerTests.allTests),
  ]
}
#endif
