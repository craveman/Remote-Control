//
//  PointsStepper.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

struct PointsStepper: View {
    let rs = RemoteService.shared
    var pType: PersonType = .none
    @Binding var score: Int
    @State private var isActive = false
    @State private var width = halfSizeButton()
    var minusHeight = CGFloat(heightOfButton())
    var plusHeight = CGFloat(heightOfButton() * 2)
    
    func thenDeactivate(_ timeout: Double = 1.1) -> Void {
        withDelay({
            self.isActive = false
        }, timeout)
    }
    
    func getPerson() -> RemoteService.PersonsManagement.Person {
    switch self.pType {
        case .left:
            return self.rs.persons.left
            case .right:
            return self.rs.persons.right
        default:
            return self.rs.persons.none
        }
    }
    
    let pSize = getButtonFrame(.doubleHeight)
    let mSize = getButtonFrame(.basic)
    
    var body: some View {
        VStack {
            Button(action: {
                print("- Button Pushed")
                if (self.score == 0) {
                    return
                }
                
                self.getPerson().score = UInt8(self.score - 1)
                self.score -= 1
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
                if (self.score == 99) {
                    return
                }
                print("+ Button Pushed")
                self.getPerson().score = UInt8(self.score + 1)
                self.score += 1
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
    @State static var score = 0
    static var previews: some View {
        PointsStepper(pType: .none, score: $score)
    }
}

func withDelay(_ callback: @escaping () -> Void, _ timeout: TimeInterval = 1) {
    Timer.scheduledTimer(withTimeInterval: timeout, repeats: false) {_ in
        callback()
    }
}
