//
//  SwiftUITools.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 18.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

var bounds = UIScreen.main.bounds
var width = bounds.size.width
var height = bounds.size.height
var primaryColor: Color = .black

struct SwiftUITools: View {
    var body: some View {
        dinFont(Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/))
    }
}

struct SwiftUITools_Previews: PreviewProvider {
    static var previews: some View {
        SwiftUITools()
    }
}


func heightOfButton() -> CGFloat {
    return height / 10
}

func halfSizeButton() -> CGFloat {
    return width / 2
}


func fullSizeButton() -> CGFloat {
    return width
}

func dinFont(_ view: Text, _ size: CGFloat = UIGlobals.appDefaultFontSize) -> Text {
    return view.font(Font.custom("DIN Alternate", size: size).bold())
}

func primaryColor(_ text: Text) -> Text {
    return text.foregroundColor(primaryColor)
}

public enum ButtonType {
    case basic
    case withImage
    case fullWidth
    case withImageFullWidth
    case doubleHeight
}

func getButtonFrame(_ size: ButtonType) -> (
    minWidth: CGFloat,
    idealWidth: CGFloat,
    maxWidth: CGFloat,
    minHeight: CGFloat,
    idealHeight: CGFloat,
    maxHeight: CGFloat,
    alignment: Alignment) {
    var h = heightOfButton(), w = halfSizeButton()
    switch size {
    case .withImageFullWidth:
         w = fullSizeButton()
        h = height / 6
    case .fullWidth:
        w = fullSizeButton()
    case .doubleHeight:
        h = 2 * h
    case .withImage:
        h = height / 6
    default:
        break
    }
    
    return (minWidth: w, idealWidth: w, maxWidth: w, minHeight: h, idealHeight: h, maxHeight: h, alignment: .center)
}

func getSubScreenHeight() -> CGFloat {
    return 320
}

final class TimerResponder: ObservableObject {
    @Published private(set) var finished: Bool = false
    @Published private(set) var tick: UInt32 = 0
    @Published private(set) var milisecondsLeft: UInt32 = 0
    private var emulationInterval: Timer = Timer()
    private(set) var interval: Double = 0.01
    private(set) var step: UInt32 = 10
    init(milisecondsLeft: UInt32 = 1, step: UInt32 = UInt32(1000)) {
        self.interval = Double(Int(step) / 1000)
        self.step = step
    }
    
    public func start() {
        self.finished = false
        self.emulationInterval = Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(fireTimer), userInfo: nil, repeats: true)
    }
    
    public func start(from ms: UInt32) {
        self.set(ms: ms)
        self.start()
    }
    
    public func set(ms: UInt32) {
        self.milisecondsLeft = ms
    }
    
    public func stop() {
        self.finished.toggle()
        emulationInterval.invalidate()
    }
    
    @objc func fireTimer() -> Void {
        self.tick += 1
        if (milisecondsLeft <= UInt32(interval)) {
            milisecondsLeft = UInt32(0)
            self.stop()
            return
        }
        milisecondsLeft -= step
    }
}


struct Passthrough<Content>: View where Content: View {

    let content: () -> Content

    init(@ViewBuilder content: @escaping () -> Content) {
        self.content = content
    }

    var body: some View {
        content()
    }

}

