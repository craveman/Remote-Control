//
//  FightSectionSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct FightSectionSwiftUIView: View {
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    VStack(spacing: 0) {
      FightTabsSelectorsUIView(selectedTab: $insp.fightSwitchActiveTab)
      if insp.fightSwitchActiveTab == 0 {
        PointsSwiftUIView()
      } else if insp.fightSwitchActiveTab == 1 {
        TimersSwiftUIView().environmentObject(settings)
      }
    }
    
  }
}

struct FightSectionSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    FightSectionSwiftUIView()
  }
}
