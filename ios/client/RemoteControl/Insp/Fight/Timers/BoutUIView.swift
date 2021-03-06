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


struct EnthernetBoutButton: View {
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  @State var showModal = false
  var body: some View {
    CommonModalButton(imageName: "antenna.radiowaves.left.and.right", imageColor: nil, buttonType: .special, text: "bout",
                      action: { Vibration.impact() }, onDismiss: {},
                      border: Color.clear, showModal: $showModal) {
                        EthernetBoutModalContentUIView()
                          .environmentObject(settings)
                          .environmentObject(insp)
    }
  }
}

struct BoutButton: View {
  @EnvironmentObject var insp: InspSettings
  var body: some View {
    VStack{
      if (insp.isEthernetMode) {
        EnthernetBoutButton()
      } else {
        ResetBoutButton()
      }
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
        ConfirmModalButton(vibrate: false, action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "cancel",
           color: primaryColor, imageName: "multiply")
          .padding([.vertical])
          .frame(width: width/2)
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
          rs.competition.reset()
          rs.persons.resetNames()
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

struct EthBoutUIView: View {
  var body: some View {
    EthernetBoutModalContentUIView()
  }
}

struct ResetBoutUIView_Previews: PreviewProvider {
  static var previews: some View {
    ResetBoutUIView()
  }
}

struct EthBoutUIView_Previews: PreviewProvider {
  static var previews: some View {
    EthBoutUIView().environmentObject(FightSettings())
      .environmentObject(InspSettings())
  }
}

struct EthBoutButtonView_Previews: PreviewProvider {
  static var previews: some View {
    EnthernetBoutButton()
      .environmentObject(FightSettings())
      .environmentObject(InspSettings())
  }
}
