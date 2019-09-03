
import XCTest

#if !canImport(ObjectiveC)
public func allTests() -> [XCTestCaseEntry] {
  return [
    testCase(SM02Tests.allTests),
    testCase(PingBroadcastServiceTests.allTests),
    testCase(IntegrationTests.allTests),
  ]
}
#endif
