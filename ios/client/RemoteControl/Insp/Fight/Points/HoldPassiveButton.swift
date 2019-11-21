//
//  HoldPassiveButton.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct HoldPassiveButton: View {
    var body: some View {
        Button(action: {
            print("Hold Passive")
        }){ primaryColor(dinFont(Text("hold passive"))) }
    }
}

struct HoldPassiveButton_Previews: PreviewProvider {
    static var previews: some View {
        HoldPassiveButton()
    }
}
