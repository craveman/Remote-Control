//
//  UIGlobals.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//
import Foundation
import UIKit

class UIGlobals {
    static let buttonCornerRadius = CGFloat(5)
    static let cardCornerRadius = CGFloat(10)
    static let activeCardBorder = (color: CGColor(#colorLiteral(red: 0, green: 0.4784313725, blue: 1, alpha: 1)),  width: CGFloat(2))
    static let activeButtonBorder = (color: CGColor(#colorLiteral(red: 0.4745098054, green: 0.8392156959, blue: 0.9764705896, alpha: 1)),  width: CGFloat(4))
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
            self.timerIsRunning = false;
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
            state.reduceTime(step);
        }
        runState(state);
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



fileprivate let step = 1000;
fileprivate let state = UIState()
let StateObserver = MyObserver(state)

func stateRunner () -> Void {
    runState(state)
    Utils.delay({state.toggleTimer()}, ms: 15*1000)
}



