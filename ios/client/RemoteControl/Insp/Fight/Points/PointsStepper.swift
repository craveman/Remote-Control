//
//  PointsStepper.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

let MAX_SCORE = 50

struct PointsStepper: View {
  
  var pType: PersonType = .none
  @EnvironmentObject var settings: FightSettings
  @State private var plusIsActive = false
  @State private var minusIsActive = false
  var width = halfSizeButton()
  var minusHeight = CGFloat(heightOfButton())
  var plusHeight = CGFloat(heightOfButton() * 2)
  
  let pSize = getButtonFrame(.doubleHeight)
  
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
  
  func thenDeactivatePlus (_ timeout: Double = 1.1) -> Void {
    withDelay({
      self.plusIsActive = false
    }, timeout)
  }
  
  
  func thenDeactivateMinus (_ timeout: Double = 1.1) -> Void {
    withDelay({
      self.minusIsActive = false
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
        self.minusIsActive = true
        Vibration.on()
        self.thenDeactivateMinus()
      }) {
        primaryColor(dinFont(Text("-"), 50)).padding().frame(width: pSize.idealWidth, height: pSize.idealHeight * 0.75, alignment: pSize.alignment)
      }
      .disabled(minusIsActive)
      .background(minusIsActive ? UIGlobals.disabledButtonBackground_SUI: nil)
      .border(Color.gray, width: 0.5)
      Button(action: {
        if (self.getScore() == MAX_SCORE) {
          return
        }
        print("+ Button Pushed")
        self.setScore(self.getScore() + 1)
        self.plusIsActive = true
        Vibration.on()
        self.thenDeactivatePlus()
        
      }) {
        primaryColor(dinFont(Text("+"), 50)).frame(width: pSize.idealWidth, height: pSize.idealHeight * 1.25, alignment: pSize.alignment)
      }
      .disabled(plusIsActive)
      .background(plusIsActive ? UIGlobals.disabledButtonBackground_SUI: nil)
      .border(Color.gray, width: 0.5)
    }
  }
}

struct PointsStepper_Previews: PreviewProvider {
  
  static var previews: some View {
    PointsStepper(pType: .none)
  }
}
