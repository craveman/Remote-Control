//
//  HoldPassiveButton.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct HoldPassiveButton: View {
    var frame = getButtonFrame(.basic)
    var body: some View {
        Button(action: {
            print("Hold Passive")
        }){ primaryColor(dinFont(Text("hold passive"))) }.frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center).border(Color.gray, width: 0.5)
    }
}

struct HoldPassiveButton_Previews: PreviewProvider {
    static var previews: some View {
        HoldPassiveButton()
    }
}
