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
        print(index)
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


struct InspTabSelector: View {
    var title: String = "Button"
    var action: () -> Void
    var isSelected: Bool = false
    var size = getButtonFrame(.basic)
    var body: some View {
        Button(action: self.action) {
            if !self.isSelected {
                Text(title).accentColor(.black).scaledFont().fixedSize()
            } else {
                Text(title).accentColor(.white).scaledFont().fixedSize()
            }
         }
        .frame(width: self.size.idealWidth, height: self.size.idealHeight, alignment: self.size.alignment)
        .background(self.isSelected ? UIGlobals.activeButtonBackground_SUI : nil)
    }
}


struct FightTabsSelectorsUIView_Previews: PreviewProvider {
    @State static var selectedTab = 0
    static var previews: some View {
        FightTabsSelectorsUIView(selectedTab: $selectedTab)
    }
}
