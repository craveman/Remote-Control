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
  var body: some View {
    CommonModalButton(imageName: "arrow.2.circlepath", imageColor: nil, buttonType: .special, text: "reset bout", action: {
      print("ResetBoutButton:action")
    }, onDismiss: {
      print("ResetBoutButton:onDismiss")
    }, content: {
      ResetBoutModalContentUIView().environmentObject(self.settings)
    })
  }
}


struct ResetBoutModalContentUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
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
          rs.competition.reset()
          self.settings.resetBout()
          self.presentationMode.wrappedValue.dismiss()
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
