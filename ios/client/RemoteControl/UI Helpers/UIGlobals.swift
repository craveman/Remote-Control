//
//  UIGlobals.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//
import Foundation
import UIKit
import SwiftUI

var inspFontName: String = "DIN Alternate"

class UIGlobals {
  static let timerFontSize: CGFloat = 64
  static let popupContentFontSize: CGFloat = 36
  static let appDefaultFontSize: CGFloat = 22
  static let appSmallerFontSize: CGFloat = 16
  
  static let buttonCornerRadius = CGFloat(0)
  static let cardCornerRadius = CGFloat(0)
  static let activeCardBorder = (color: CGColor(#colorLiteral(red: 0, green: 0.4784313725, blue: 1, alpha: 1)),  width: CGFloat(2))
  static let activeButtonBorder = (color: CGColor(#colorLiteral(red: 0.4745098054, green: 0.8392156959, blue: 0.9764705896, alpha: 1)),  width: CGFloat(4))
  
  static let activeButtonBackground_SUI = Color(#colorLiteral(red: 0.3765910268, green: 0.3763456941, blue: 0.3850344718, alpha: 1))
  static let headerBackground_SUI = Color(#colorLiteral(red: 0.3765910268, green: 0.3763456941, blue: 0.3850344718, alpha: 1))
  static let disabledButtonBackground_SUI = Color(#colorLiteral(red: 0, green: 0.5049814582, blue: 1, alpha: 0.5))
}

class UIState: NSObject {
  @objc dynamic var timerMs = NSInteger(3 * 60 * 1000)
  @objc dynamic var timerIsRunning = false
  
  func toggleTimer() {
    self.timerIsRunning = false
  }
  func timerToString() -> String {
    return ""
  }
  
  func reduceTime(_ ms: Int) {
    let (diff, _) = timerMs.subtractingReportingOverflow(ms)
    if(diff < 0) {
      self.resetTime(0)
      self.timerIsRunning = false
    }
  }
  
  func resetTime(_ ms: Int) {
    let _ = timerMs.subtractingReportingOverflow(timerMs.distance(to: ms))
  }
}


fileprivate func runState(_ state: UIState) {
  Utils.delay({
    //        print("\(state.timerIsRunning):\(state.timerMs)")
    if state.timerIsRunning {
      print("running")
      state.reduceTime(step)
    }
    runState(state)
  }, ms: step)
}


class MyObserver: NSObject {
  @objc var objectToObserve: UIState
  var observation: NSKeyValueObservation?
  
  init(_ state: UIState) {
    objectToObserve = state
    super.init()
    
    observation = observe(\.objectToObserve.timerMs, options: [.old, .new]
    ) { object, change in
      print("Timer changed from: \(change.oldValue!), updated to: \(change.newValue!); Running: \(self.objectToObserve.timerIsRunning)")
    }
  }
  
  
}

fileprivate let step = 1000
fileprivate let state = UIState()
let StateObserver = MyObserver(state)

func stateRunner () -> Void {
  runState(state)
  Utils.delay({state.toggleTimer()}, ms: 15*1000)
}

@discardableResult func withDelay (_ callback: @escaping () -> Void, _ timeout: TimeInterval = 1) -> Timer {
  return Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) {_ in
    callback()
  }
}

@available(iOS 13, macCatalyst 13, tvOS 13, watchOS 6, *)
struct ScaledFont: ViewModifier {
    @Environment(\.sizeCategory) var sizeCategory
    var name: String
    var size: CGFloat

    func body(content: Content) -> some View {
       let scaledSize = UIFontMetrics.default.scaledValue(for: size)
        return content.font(.custom(name, size: scaledSize))
    }
}

@available(iOS 13, macCatalyst 13, tvOS 13, watchOS 6, *)
extension View {
    func scaledFont(name: String = inspFontName, size: CGFloat = 24) -> some View {
        return self.modifier(ScaledFont(name: name, size: size))
    }
}


