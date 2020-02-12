//
//  FightTabsSelectorsUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 13.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct FightTabsSelectorsUIView: View {
  private(set) var tabs = ["Main", "Timers"]
  @Binding var selectedTab: Int
  
  func doSelect(_ index: Int) {
    self.selectedTab = index
  }
  
  func isSelected(_ index: Int) -> Bool {
    return self.selectedTab == index
  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.tabs[index])"
  }
  
  var body: some View {
    HStack(spacing: 0) {
      ForEach(0..<tabs.count) { (i: Int) in
        InspTabSelector(title: self.getTitle(i), action: { self.doSelect(i) }, isSelected: self.isSelected(i))
      }
    }
  }
}

struct FightTabsSelectorsUIView_Previews: PreviewProvider {
  @State static var selectedTab = 0
  static var previews: some View {
    FightTabsSelectorsUIView(selectedTab: $selectedTab)
  }
}
