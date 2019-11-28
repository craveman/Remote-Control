//
//  FightSectionSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct FightSectionSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  
  var body: some View {
    VStack(spacing: 0) {
      FightTabsSelectorsUIView(selectedTab: $settings.fightSwitchActiveTab)
      if settings.fightSwitchActiveTab == 0 {
        PointsSwiftUIView()
      } else if settings.fightSwitchActiveTab == 1 {
        TimersSwiftUIView()
      }
    }
    
  }
}

struct FightSectionSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    FightSectionSwiftUIView()
  }
}
