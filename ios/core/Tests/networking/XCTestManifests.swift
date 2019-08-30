
import XCTest

#if !canImport(ObjectiveC)
public func allTests() -> [XCTestCaseEntry] {
  return [
    testCase(NetworkTests.allTests),
    testCase(PingCatcherServiceTests.allTests),
  ]
}
#endif
