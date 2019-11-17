//
//  SwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VideoButton: View {
    var body: some View {
        Button(action: {
            print("View video")
        }){ Text("view video")}.font(Font.custom("DIN Alternate", size: 20).bold()).accentColor(.black)
    }
}

struct VideoButton_Previews: PreviewProvider {
    static var previews: some View {
        VideoButton()
    }
}
