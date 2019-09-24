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
            callback();
        }
    }
    
    static func delay(_ callback: @escaping () -> Void, seconds delay: Int) {
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(delay)) {
//            print("\(delay) seconds later")
            callback();
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

