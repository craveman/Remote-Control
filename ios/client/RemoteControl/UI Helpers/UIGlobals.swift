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
  static let cardBoardBackground = Color(#colorLiteral(red: 0.8039215803, green: 0.8039215803, blue: 0.8039215803, alpha: 1))
  static let modalSheetBackground = Color(#colorLiteral(red: 0.8039215803, green: 0.8039215803, blue: 0.8039215803, alpha: 1))
  static let cardCornerRadius = CGFloat(0)
  static let activeCardBorder = (color: CGColor(#colorLiteral(red: 0, green: 0.4784313725, blue: 1, alpha: 1)),  width: CGFloat(2))
  static let activeButtonBorder = (color: CGColor(#colorLiteral(red: 0.4745098054, green: 0.8392156959, blue: 0.9764705896, alpha: 1)),  width: CGFloat(4))
  
  static let insp_blue_SUI = Color(#colorLiteral(red: 0, green: 0.4784313725, blue: 1, alpha: 1))
  static let activeBackground_SUI = Color(#colorLiteral(red: 0.9920703769, green: 0.9922123551, blue: 0.9920392632, alpha: 0.01))
  static let activeButtonBackground_SUI = Color(#colorLiteral(red: 0.3765910268, green: 0.3763456941, blue: 0.3850344718, alpha: 1))
  static let headerBackground_SUI = Color(#colorLiteral(red: 0.3765910268, green: 0.3763456941, blue: 0.3850344718, alpha: 1))
  static let disabledButtonBackground_SUI = Color(#colorLiteral(red: 0, green: 0.5049814582, blue: 1, alpha: 0.5))
}

@discardableResult func withDelay (_ callback: @escaping () -> Void, _ timeout: TimeInterval = 1) -> Timer {
  return Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) {timer in
    timer.invalidate()
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


