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
    @State var timer = UInt32(181000)
    private var responder = TimerResponder()
    
    var body: some View {
        VStack {
            FightTabsSelectorsUIView(selectedTab: $selectedTab)
            if $selectedTab.wrappedValue == 0 {
                PointsSwiftUIView(timer: $timer)
            } else {
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

func dinFont(_ view: Text, _ size: CGFloat = 20) -> Text {
    return view.font(Font.custom("DIN Alternate", size: size).bold())
}
