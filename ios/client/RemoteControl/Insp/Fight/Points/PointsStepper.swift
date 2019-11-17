//
//  PointsStepper.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct PointsStepper: View {
    var body: some View {
        VStack {
            Button(action: {
                print("- Button Pushed")
            }) {
                Text("-")
            }.font(Font.custom("DIN Alternate", size: 60).bold()).accentColor(.black)
            .padding(.vertical, 32)
            .padding(.horizontal, 50)
            Button(action: {
                print("+ Button Pushed")
            }) {
                Text("+")
            }.font(Font.custom("DIN Alternate", size: 60).bold()).accentColor(.black)
            .padding(.vertical, 50)
            .padding(.horizontal, 50)
        }
    }
}

struct PointsStepper_Previews: PreviewProvider {
    static var previews: some View {
        PointsStepper()
    }
}
