//
//  TimersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

fileprivate struct TimersSetters: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    HStack(spacing: 0) {
      SetPassiveButtonSwiftUIView(onDismiss: {
//        self.settings.fightSwitchActiveTab = 0
      })
      SetTimeButtonSwiftUIView(onDismiss: {
//        self.settings.fightSwitchActiveTab = 0
      })
    }
  }
}



struct TimersSwiftUIView: View {
  
  var body: some View {
    VStack(spacing: 0){
      Divider()
      ResetBoutButton()
      PauseSetters()
      TimersSetters()
    }
    .frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: .infinity, alignment: .top)
    
  }
}

struct TimersSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    TimersSwiftUIView()
  }
}
