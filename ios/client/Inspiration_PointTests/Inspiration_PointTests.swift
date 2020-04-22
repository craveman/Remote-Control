//
//  Inspiration_PointTests.swift
//  Inspiration_PointTests
//
//  Created by Sergei Andreev on 22.04.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import XCTest
@testable import Inspiration_Point

class Inspiration_PointTests: XCTestCase {
  
  override func setUpWithError() throws {
    // Put setup code here. This method is called before the invocation of each test method in the class.
  }
  
  override func tearDownWithError() throws {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
  }
  
  func testExample() throws {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
    
  }
  
  
  func testFileNameConverter() throws {
    
    XCTAssertEqual(FileNameConverter.getTitle("11_10#12_13_15.mp4"), "11 - 10  12:13:15")
    XCTAssertEqual(FileNameConverter.getTitle("11_10#9_30_00.mp4"), "11 - 10  9:30:00")
    XCTAssertEqual(FileNameConverter.getTitle("1_1#12_13_15.mp4"), "1 - 1  12:13:15")
    XCTAssertEqual(FileNameConverter.getTitle("0_49#12_13_15.mp4"), "0 - 49  12:13:15")
    XCTAssertEqual(FileNameConverter.getTitle("random_name.mp4"), "random_name")
    XCTAssertEqual(FileNameConverter.getTitle("random_name.mp4.zip"), "random_name")
    XCTAssertEqual(FileNameConverter.getTitle("jljkfl98a4964jdhaksj"), "jljkfl98a4964jdhaksj")
    
  }
  
  
  func testPerformanceExample() throws {
    // This is an example of a performance test case.
    measure {
      // Put the code you want to measure the time of here.
    }
  }
  
}
