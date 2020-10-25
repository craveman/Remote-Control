//
//  SetPeriodButtonSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 28.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct SetPeriodButtonSwiftUIView: View {
    var body: some View {
        PeriodSetter()
    }
}

fileprivate let maxPeriod: Int = INSPIRATION_MAX_PERIOD

fileprivate struct PeriodSetter: View {
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  let frame = getButtonFrame(.special)
  func getPeriodString() -> String {
    let period = self.settings.period;
    if insp.isEthernetMode && rs.competition.teamFight {
      return "\(period)"
    }
    
    guard period > 1 else {
      return "1..."
    }
    
    guard period < maxPeriod else {
      return "...\(maxPeriod)"
    }
    
    return "..\(period).."
  }
  var body: some View {
    Button(action: {self.showModal.toggle()}) {
      VStack {
        primaryColor(dinFont(Text(self.getPeriodString()), 36))
        primaryColor(dinFont(Text("set period")))
        }.padding().frame(width: width)
        
    }
    .disabled(insp.isEthernetMode && rs.competition.teamFight)
    .frame(width: width)
    .sheet(isPresented: self.$showModal, onDismiss: {
    }) {
      PeriodModalContent()
        .environmentObject(self.settings)
        .background(UIGlobals.modalSheetBackground)
        .edgesIgnoringSafeArea(.bottom)
    }
  }
}

fileprivate func perIndexToPeriod(_ index: Int) -> UInt8 {
  return UInt8(index + 1)
}


fileprivate func periodToPerIndex(_ period: UInt8) -> Int {
  return Int(period - 1)
}

fileprivate struct PeriodModalContent: View {
  
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  @State var nextLocked = false
  @State var perIndex = periodToPerIndex(max(rs.competition.period, 1))
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Period")
      Spacer()
      CommonPicker(selected: $perIndex, options: Array(1...maxPeriod).map({"\($0)"})).frame(width: width/100*80)
      Spacer()
      VStack(spacing: 0) {
        Divider()
        Button(action: {
          self.perIndex += 1
          self.settings.setPeriod(perIndexToPeriod(self.perIndex))
          Vibration.on()
        }) {
          primaryColor(dinFont(Text("next period"))).padding()
        }
          .frame(width: width)
          .disabled(perIndex >= maxPeriod - 1)
      }.opacity(perIndex < maxPeriod - 1 ? 1 : 0)
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(vibrate: false, action: {
          self.settings.setPeriod(perIndexToPeriod(self.perIndex))
          Vibration.on()
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
    }
  }
}


struct SetPeriodButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        SetPeriodButtonSwiftUIView()
          .environmentObject(FightSettings())
          .environmentObject(InspSettings())
    }
}
