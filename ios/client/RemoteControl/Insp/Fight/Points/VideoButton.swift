//
//  SwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VideoButton: View {
    var frame = getButtonFrame(.basic)
    var body: some View {
        Button(action: {
            print("View video")
        }){ primaryColor(dinFont(Text("view video"))) }.frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center).border(Color.gray, width: 0.5)
    }
}

struct VideoButton_Previews: PreviewProvider {
    static var previews: some View {
        VideoButton()
    }
}
