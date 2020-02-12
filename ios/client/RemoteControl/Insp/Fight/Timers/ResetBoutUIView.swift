//
//  ResetBoutUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct ResetBoutButton: View {
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  @State var showModal = false
  var body: some View {
    CommonModalButton(imageName: "arrow.2.circlepath", imageColor: nil, buttonType: .special, text: "reset bout",
                      action: { Vibration.on() }, onDismiss: {},
                      border: Color.clear, showModal: $showModal) {
      ResetBoutModalContentUIView().environmentObject(self.settings).environmentObject(self.insp)
    }
  }
}

struct ResetBoutModalContentUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  var body: some View {
    VStack{
      Spacer()
      Image(systemName: "arrow.2.circlepath").resizable().scaledToFit()
        .frame(width: 200, height: 180).foregroundColor(primaryColor)
      primaryColor(dinFont(Text("reset bout"), UIGlobals.popupContentFontSize))
        .padding(.top)
      Spacer()
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "cancel",
           color: primaryColor, imageName: "multiply")
          .padding([.vertical])
          .frame(width: width/2)
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
          rs.competition.reset()
          self.settings.resetBout()
          self.insp.onReset()
        }, text: "confirm", color: .green)
          .padding([.vertical])
          .frame(width: width/2)
      }
    }
  }
}

struct ResetBoutUIView: View {
  var body: some View {
    ResetBoutModalContentUIView()
  }
}

struct ResetBoutUIView_Previews: PreviewProvider {
  static var previews: some View {
    ResetBoutUIView()
  }
}
