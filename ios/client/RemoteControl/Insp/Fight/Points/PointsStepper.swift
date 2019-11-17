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
    var body: some View {
        VStack {
            Button(action: {
                print("- Button Pushed")
                if (self.score == 0) {
                    return
                }
                
                self.rs.setScore(for: self.pType, UInt8(self.score - 1))
                self.score -= 1
            }) {
                Text("-")
            }.font(Font.custom("DIN Alternate", size: 60).bold()).accentColor(.black)
            .padding(.vertical, 32)
            .padding(.horizontal, 50)
            Button(action: {
                if (self.score == 99) {
                    return
                }
                print("+ Button Pushed")
                self.rs.setScore(for: self.pType, UInt8(self.score + 1))
                self.score += 1
            }) {
                Text("+")
            }.font(Font.custom("DIN Alternate", size: 60).bold()).accentColor(.black)
            .padding(.vertical, 50)
            .padding(.horizontal, 50)
        }
    }
}

struct PointsStepper_Previews: PreviewProvider {
    @State static var score = 0
    static var previews: some View {
        PointsStepper(pType: .none, score: $score)
    }
}
