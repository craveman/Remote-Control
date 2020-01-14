//
//  RemoteControlTests.swift
//  RemoteControlTests
//
//  Created by Artem Labazin on 15/08/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import XCTest
@testable import RemoteControl

class RemoteControlTests: XCTestCase {
  private let state: TimerState
  
    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
  

  fileprivate func runTimerRaceCase(_ caseNum: UInt8) {

    switch caseNum {
    case 0:
      
      // case 0 - strange

       withDelay({
         self.state = .suspended
         print("race timer off 1")
       }, 0.01)
      
      withDelay({
        self.state = .suspended
        print("race timer off 2")
      }, 0.21)

      withDelay({
        self.state = .running
        print("race timer on 1")
      }, 0.41)
      
    case 1:

      // case 1 - normal
      
      withDelay({
        self.state = .running
        print("race timer on 1")
      }, 0.15)
      
      withDelay({
        self.state = .running
        print("race timer on 2")
      }, 0.35)
      
    case 2:
      // case 2 - race
      
      withDelay({
        self.state = .suspended
        print("race timer off")
      }, 0.10)

      withDelay({
        self.state = .running
        print("race timer on")
      }, 0.30)
    default:
      return
    }
    
    withDelay({
      self.state = .suspended
      print("stop timer")
    }, 5)
  }
  

}
