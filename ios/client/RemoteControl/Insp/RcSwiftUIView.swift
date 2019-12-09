//
//  RcSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct RcSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  
  var body: some View {
    VStack(spacing: 0){
      if !insp.isConnected {
        ZStack() {
          primaryColor(dinFont(Text("disconnected"), UIGlobals.appSmallerFontSize))
            .frame(width: width)
            .background(Color.red)
        }
        
      }
      TabView(selection: $settings.tab) {
        CardsSwiftUIView()
          .tabItem {
            Image(systemName: "square.fill")
            Text("Cards")
        }.tag(0)
        
        FightSectionSwiftUIView()
          .tabItem {
            Image(systemName: "circle.fill")
            Text("Fight")
        }.tag(1)
        
        SettingsSwiftUIView()
          .tabItem {
            Image(systemName: "arrowtriangle.down.fill")
            Text("SettingsTab")
        }.tag(2)
      }
      .font(.headline)
    }
  }
}

struct RcSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    RcSwiftUIView()
  }
}
