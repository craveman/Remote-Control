import XCTest
@testable import Sm02Client

final class Sm02ClientTests: XCTestCase {
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct
        // results.
        XCTAssertEqual(Sm02Client().text, "Hello, World!")
    }

    static var allTests = [
        ("testExample", testExample),
    ]
}
