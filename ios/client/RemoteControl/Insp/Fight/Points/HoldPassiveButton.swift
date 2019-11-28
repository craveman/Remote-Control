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
  var frame = getButtonFrame(.basic)
  var body: some View {
    Button(action: {
      print("Hold Passive")
      self.settings.holdPassive.toggle()
      rs.timer.passive.isBlocked = self.settings.holdPassive
    }){ primaryColor(dinFont(Text("hold passive"))) }
      .frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center)
      .border(Color.gray, width: 0.5)
      .background(self.settings.holdPassive ? Color.yellow.opacity(0.33) : Color.clear)
      .opacity(self.settings.showPassive ? 1 : 0)
    .disabled(!self.settings.showPassive)
  }
}

struct HoldPassiveButton_Previews: PreviewProvider {
  static var previews: some View {
    HoldPassiveButton()
  }
}
