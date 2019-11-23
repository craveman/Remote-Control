//
//  HoldPassiveButton.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct HoldPassiveButton: View {
  @State var holdPassive = false
  @EnvironmentObject var settings: FightSettings
  var frame = getButtonFrame(.basic)
  var body: some View {
    Button(action: {
      print("Hold Passive")
      self.holdPassive = !self.holdPassive
      rs.display.passive = !self.settings.showPassive
    }){ primaryColor(dinFont(Text("hold passive"))) }
      .frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center)
      .border(Color.gray, width: 0.5)
      .background(holdPassive ? Color.yellow.opacity(0.33) : Color.clear)
  }
}

struct HoldPassiveButton_Previews: PreviewProvider {
  static var previews: some View {
    HoldPassiveButton()
  }
}
