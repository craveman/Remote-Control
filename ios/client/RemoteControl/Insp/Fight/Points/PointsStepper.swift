//
//  PointsStepper.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct PointsStepper: View {
  
  var pType: PersonType = .none
  @EnvironmentObject var settings: FightSettings
  @State private var isActive = false
  var width = halfSizeButton()
  var minusHeight = CGFloat(heightOfButton())
  var plusHeight = CGFloat(heightOfButton() * 2)
  
  let pSize = getButtonFrame(.doubleHeight)
  let mSize = getButtonFrame(.basic)
  
  func getScore () -> UInt8 {
    switch pType {
    case .left:
      return settings.leftScore
    case .right:
      return settings.rightScore
    default:
      return 0
    }
  }
  
  
  func setScore (_ next: UInt8) -> Void {
    switch pType {
    case .left:
      return settings.leftScore = next
    case .right:
      return settings.rightScore = next
    default:
      return
    }
  }
  
  func thenDeactivate (_ timeout: Double = 1.1) -> Void {
    withDelay({
      self.isActive = false
    }, timeout)
  }
  
  var body: some View {
    VStack {
      Button(action: {
        print("- Button Pushed")
        if (self.getScore() == 0) {
          return
        }
        self.setScore(self.getScore() - 1)
        self.isActive = true
        self.thenDeactivate()
      }) {
        primaryColor(dinFont(Text("-"), 50))
      }
      .frame(width: mSize.idealWidth, height: mSize.idealHeight, alignment: mSize.alignment)
      .disabled(isActive)
      .background(isActive ? UIGlobals.disabledButtonBackground_SUI: nil)
      .border(Color.gray, width: 0.5)
      Button(action: {
        if (self.getScore() == 99) {
          return
        }
        print("+ Button Pushed")
        self.setScore(self.getScore() + 1)
        self.isActive = true
        self.thenDeactivate()
        
      }) {
        primaryColor(dinFont(Text("+"), 50))
      }
      .frame(width: pSize.idealWidth, height: pSize.idealHeight, alignment: pSize.alignment)
      .disabled(isActive)
      .background(isActive ? UIGlobals.disabledButtonBackground_SUI: nil)
      .border(Color.gray, width: 0.5)
    }
  }
}

struct PointsStepper_Previews: PreviewProvider {
  
  static var previews: some View {
    PointsStepper(pType: .none)
  }
}

@discardableResult func withDelay (_ callback: @escaping () -> Void, _ timeout: TimeInterval = 1) -> Timer {
  return Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) {_ in
    callback()
  }
}
