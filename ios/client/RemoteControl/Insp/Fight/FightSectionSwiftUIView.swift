//
//  FightSectionSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct FightSectionSwiftUIView: View {
    @State var selectedTab = 0
    
    var body: some View {
        VStack(spacing: 0) {
            FightTabsSelectorsUIView(selectedTab: $selectedTab)
            if $selectedTab.wrappedValue == 0 {
                PointsSwiftUIView().alignmentGuide(VerticalAlignment.top, computeValue: {_ in 0})
            } else if $selectedTab.wrappedValue == 1 {
                TimersSwiftUIView().alignmentGuide(VerticalAlignment.top, computeValue: {_ in 0})
            }
        }
        
    }
}

struct FightSectionSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        FightSectionSwiftUIView()
    }
}
