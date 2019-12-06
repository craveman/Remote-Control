//
//  HoldPassiveButton.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct HoldPassiveButton: View {
  @EnvironmentObject var settings: FightSettings
  var frame = getButtonFrame(.fullWidth)
  var body: some View {
    VStack(spacing: 0) {
      Button(action: {
        print("Hold Passive")
        self.settings.holdPassive.toggle()
        Vibration.on()
        rs.timer.passive.isBlocked = self.settings.holdPassive
      }){ primaryColor(dinFont(Text("hold passive"))) }
        .frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center)
        .background(self.settings.holdPassive ? Color.yellow.opacity(0.33) : Color.clear)
      .disabled(!self.settings.showPassive)
      Divider()
    }.opacity(self.settings.showPassive ? 1 : 0)
    
  }
}

struct HoldPassiveButton_Previews: PreviewProvider {
  static var previews: some View {
    HoldPassiveButton()
  }
}
