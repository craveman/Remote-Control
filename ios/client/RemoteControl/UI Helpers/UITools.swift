//
//  UITools.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import UIKit

class Utils {
    
    static func delay(_ callback: @escaping () -> Void, ms delay: Int) {
        DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(delay)) {
//            print("\(delay) milliseconds later")
            callback()
        }
    }
    
    static func delay(_ callback: @escaping () -> Void, seconds delay: Int) {
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(delay)) {
//            print("\(delay) seconds later")
            callback()
        }
    }
}

class UITools {
    
    static func disableButtonForTime(_ sender: UIButton, _ delay: Int = 1000) {
        let savedEnabledState = sender.isEnabled,
        savedHighlightState = sender.isHighlighted,
        savedBorderState = (sender.layer.borderColor, sender.layer.borderWidth)
        sender.isEnabled = false
        sender.isHighlighted = true
        let (color, width) = UIGlobals.activeCardBorder
        sender.layer.borderColor = color
        sender.layer.borderWidth = width
        Utils.delay({
            sender.isEnabled = savedEnabledState
            sender.isHighlighted = savedHighlightState
            let (color, width) = savedBorderState
            sender.layer.borderColor = color
            sender.layer.borderWidth = width
        }, ms: delay)
    }
    
}

extension UIView {
    
  func addTopBorder(with color: UIColor?, andWidth borderWidth: CGFloat) {
      let border = UIView()
      border.backgroundColor = color
      border.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
      border.frame = CGRect(x: 0, y: 0, width: frame.size.width, height: borderWidth)
      addSubview(border)
  }

  func addBottomBorder(with color: UIColor?, andWidth borderWidth: CGFloat) {
      let border = UIView()
      border.backgroundColor = color
      border.autoresizingMask = [.flexibleWidth, .flexibleTopMargin]
      border.frame = CGRect(x: 0, y: frame.size.height - borderWidth, width: frame.size.width, height: borderWidth)
      addSubview(border)
  }

  func addLeftBorder(with color: UIColor?, andWidth borderWidth: CGFloat) {
      let border = UIView()
      border.backgroundColor = color
      border.frame = CGRect(x: 0, y: 0, width: borderWidth, height: frame.size.height)
      border.autoresizingMask = [.flexibleHeight, .flexibleRightMargin]
      addSubview(border)
  }

  func addRightBorder(with color: UIColor?, andWidth borderWidth: CGFloat) {
      let border = UIView()
      border.backgroundColor = color
      border.autoresizingMask = [.flexibleHeight, .flexibleLeftMargin]
      border.frame = CGRect(x: frame.size.width - borderWidth, y: 0, width: borderWidth, height: frame.size.height)
      addSubview(border)
  }
}

func getMinutes(_ timer: UInt32) -> String {
  let m = Int(floor(Double(timer)/60000.0))
  return "\(m > 9 ? "" : "0")\(m)"
}

func getSeconds(_ timer: UInt32) -> String {
  let s = Int(floor(Double(timer)/1000.0)) % 60
  return "\(s > 9 ? "" : "0")\(s)"
}

func getTimeString(_ timer: UInt32, _ showMs: Bool = false) -> String {
  if (showMs && timer < 10000) {
    let sec = Double(timer) / 1000
    let formatted = String(format: "%.1f", sec)
    return "0:0\(formatted)"
  }
  
  return "\(getMinutes(timer)):\(getSeconds(timer))"
}

extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}
