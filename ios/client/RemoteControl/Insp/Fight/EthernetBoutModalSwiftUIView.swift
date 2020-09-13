//
//  EthernetBoutModalSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06.09.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import SwiftUI

fileprivate struct EthNextPrevView: View {
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        ConfirmModalButton(vibrate: false, action: {
          //
          rs.ethernetNextOrPrevious(next: false)
        }, text: "prev",
           color: primaryColor,
           imageName: "chevron.left")
          .padding([.vertical])
          .frame(width: width/2)
        Divider().frame(height: mediumHeightOfButton())
        ConfirmModalButton(vibrate: false, action: {
          //
          rs.ethernetNextOrPrevious(next: true)
        }, text: "next",
           color: primaryColor,
           imageName: "chevron.right")
          .padding([.vertical])
          .frame(width: width/2)
      }.frame(height: mediumHeightOfButton())
      
    }
  }
}

fileprivate struct EthDisplayView: View {
  var body: some View {
    VStack(spacing: 0) {
      primaryColor(dinFont(Text("competition")))
      primaryColor(dinFont(Text("123")))
    }
    .padding(.vertical)
    .frame(minHeight: mediumHeightOfButton())
  }
}

fileprivate struct EthCompleteView: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    HStack(spacing: 0) {
      ConfirmModalButton(vibrate: false, action: {
        //
        rs.ethernetFinishAsk()
        withDelay({
          self.settings.ethernetFightPhase = .none
          rs.ethernetNextOrPrevious(next: true)
        }, RemoteService.SYNC_INTERVAL)
        
      }, text: "complete",
         color: primaryColor,
         imageName: "checkmark.circle")
        .padding([.vertical])
        .frame(width: width)
    }.frame(height: mediumHeightOfButton())
  }
}

struct PhaseNoneMenu: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    VStack(spacing: 0) {
      if settings.ethernetFightPhase == .none {
        Divider()
        EthNextPrevView()
        Divider()
        EthDisplayView()
        Divider()
      }
    }
  }
}


struct PhaseActiveMenu: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    VStack(spacing: 0) {
      if settings.ethernetFightPhase == .active {
        Divider()
        EthCompleteView()
        Divider()
      }
    }
  }
}

struct EthernetBoutModalContentUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  var body: some View {
    VStack(spacing: 0) {
      Spacer()
      PhaseNoneMenu()
      PhaseActiveMenu()
      Spacer()
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(vibrate: false, action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "cancel",
           color: primaryColor, imageName: "chevron.left")
          .padding([.vertical])
          .frame(width: width/2)
        if settings.ethernetFightPhase != .active {
        ConfirmModalButton(action: {
          rs.competition.cyranoApply()
          self.settings.ethernetFightPhase = .active
          self.presentationMode.wrappedValue.dismiss()
        }, text: "apply", color: .green)
          .padding([.vertical])
          .frame(width: width/2)
          
        }
      }
    }
  }
}

struct EthernetBoutModalSwiftUIView: View {
  @EnvironmentObject var insp: InspSettings
  var body: some View {
    EthernetBoutModalContentUIView()
  }
}

struct EthernetBoutModalSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    EthernetBoutModalSwiftUIView()
      .environmentObject(FightSettings())
      .environmentObject(InspSettings())
  }
}
