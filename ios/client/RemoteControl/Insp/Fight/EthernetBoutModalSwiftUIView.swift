//
//  EthernetBoutModalSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 06.09.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import SwiftUI

fileprivate struct EthNextPrevView: View {
  @EnvironmentObject var settings: FightSettings
  let buttonSize = getButtonFrame(.withImage)
  
  var body: some View {
    VStack(spacing: 0) {
        HStack(spacing: 0) {
          
          CommonButton(action: {
            //
            print("eth prev", self.settings.ethernetActionIsLocked)
            rs.ethernetNextOrPrevious(next: false)
            self.settings.turnOnEthLockTimer()
            Vibration.impact()
          }, text: "prev",
          imageName: "chevron.left", imageColor: primaryColor)
          .disabled(settings.ethernetActionIsLocked)
          .background(settings.ethernetActionIsLocked ? UIGlobals.disabledButtonBackground_SUI: nil)
          .frame(width: width/2, height: buttonSize.minHeight)
          
          Divider().frame(height: buttonSize.minHeight)
          
          CommonButton(action: {
            //
            print("eth next", self.settings.ethernetActionIsLocked)
            rs.ethernetNextOrPrevious(next: true)
            self.settings.turnOnEthLockTimer()
            Vibration.impact()
          }, text: "next",
          imageName: "chevron.right", imageColor: primaryColor)
          .disabled(settings.ethernetActionIsLocked)
          .background(settings.ethernetActionIsLocked ? UIGlobals.disabledButtonBackground_SUI: nil)
          .frame(width: width/2, height: buttonSize.minHeight)
        }
    }
    .frame(width: width, height: buttonSize.minHeight)
  }
}

fileprivate struct EthDisplayView: View {
  @Binding var competitionName: String
  var body: some View {
    VStack(spacing: 0) {
      primaryColor(dinFont(Text(competitionName))).lineLimit(3)
    }
    .padding(.vertical)
    .frame(minHeight: mediumHeightOfButton())
  }
}

fileprivate struct EthCompleteView: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    HStack(spacing: 0) {
      CommonButton(action: {
        rs.ethernetFinishAsk()
        Vibration.on()
        self.settings.turnOnEthLockTimer()
      }, text: "complete",
      imageName: "checkmark.circle", imageColor: primaryColor, frame: getButtonFrame(.withImageFullWidth))
      .disabled(settings.ethernetActionIsLocked)
      .background(settings.ethernetActionIsLocked ? UIGlobals.disabledButtonBackground_SUI: nil)
    }.frame(height: mediumHeightOfButton())
  }
}

struct PhaseNoneMenu: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    VStack(spacing: 0) {
      dinFont(Text(" ")).padding().frame(width: width).background(UIGlobals.headerBackground_SUI)
      Divider()
      EthNextPrevView()
      Divider()
      if settings.ethernetFightPhase == .none {
        
      }
    }
  }
}


struct PhaseActiveMenu: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    VStack(spacing: 0) {
      dinFont(Text(" ")).padding().frame(width: width).background(UIGlobals.headerBackground_SUI)
      Divider()
      EthCompleteView()
      Divider()
    }
  }
}

struct EthControlMenu: View {
  @EnvironmentObject var settings: FightSettings
  let none = PhaseNoneMenu()
  let active = PhaseActiveMenu()
  var body: some View {
    Divider()
    EthDisplayView(competitionName: $settings.ethernetNextFightTitle)
    Divider()
    if settings.ethernetFightPhase == .none {
      none
    }
    if settings.ethernetFightPhase == .active {
      active
    }
  }
}

struct EthModalControls: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  
  func applyAction() -> Void {
    rs.competition.cyranoApply()
    self.settings.turnOnEthLockTimer()
    self.settings.syncPeriod()
    self.settings.ethernetFightPhase = .active
    self.presentationMode.wrappedValue.dismiss()
  }
  
  var body: some View {
    HStack(spacing: 0) {
      ConfirmModalButton(vibrate: false, action: {
        self.presentationMode.wrappedValue.dismiss()
      }, text: "cancel",
      color: primaryColor, imageName: "chevron.left")
      .padding([.vertical])
      .frame(width: width/2)
      if settings.ethernetFightPhase != .active {
        ConfirmModalButton(action: {
          self.applyAction()
        }, text: "apply", color: .green)
        .padding([.vertical])
        .frame(width: width/2)
          .disabled(settings.ethernetActionIsLocked)
          .background(settings.ethernetActionIsLocked ? UIGlobals.disabledButtonBackground_SUI: nil)
      }
    }
  }
}

struct EthernetBoutModalContentUIView: View {
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Bout")
      Spacer()
      EthControlMenu()
      Spacer()
      Divider()
      EthModalControls()
    }
  }
}

struct EthernetBoutModalSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    EthernetBoutModalContentUIView()
      .environmentObject(settings)
  }
}

struct EthernetBoutModalSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    EthernetBoutModalSwiftUIView()
      .environmentObject(FightSettings())
  }
}
