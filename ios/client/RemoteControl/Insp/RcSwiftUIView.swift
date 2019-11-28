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
  
  var body: some View {
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

struct RcSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    RcSwiftUIView()
  }
}
