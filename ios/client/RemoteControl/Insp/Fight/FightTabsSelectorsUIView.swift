//
//  FightTabsSelectorsUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 13.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct FightTabsSelectorsUIView: View {
    @State private var tabs = ["Main", "Timers"]
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
        HStack {
            ForEach(0..<tabs.count) { (i: Int) in
                InspTabSelector(title: self.getTitle(i), action: { self.doSelect(i) }, isSelected: self.isSelected(i))
            }
        }.frame(minWidth: CGFloat(tabs.count * 100), idealWidth: .infinity, maxWidth: .infinity, minHeight: 20, idealHeight: 48, maxHeight: 48, alignment: .center)
    }
}


struct InspTabSelector: View {
    var title: String = "Button"
    var action: () -> Void
    var isSelected: Bool = false
    
    var body: some View {
        Button(action: self.action) {
            if !self.isSelected {
                Text(title).accentColor(.black).scaledFont()
            } else {
                Text(title).accentColor(.white).background(Color(#colorLiteral(red: 0, green: 0.5049814582, blue: 1, alpha: 1))).scaledFont()
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
