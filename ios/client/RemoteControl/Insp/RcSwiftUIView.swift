//
//  RcSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct RcSwiftUIView: View {
    var body: some View {
      TabView {
            CardsSwiftUIView()
                .tabItem {
                    Image(systemName: "square.fill")
                    Text("Cards")
            }
            FightSectionSwiftUIView()
                .tabItem {
                    Image(systemName: "circle.fill")
                    Text("Fight")
            }.alignmentGuide(.top, computeValue: {_ in 0})
            
            SettingsSwiftUIView()
                .tabItem {
                    Image(systemName: "arrowtriangle.down.fill")
                    Text("SettingsTab")
            }
        }
        .font(.headline)
        
    }
}

struct RcSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        RcSwiftUIView()
    }
}
