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
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  let frame = getButtonFrame(.special)
  func getPeriodString() -> String {
    let period = self.settings.period;
    guard period + 1 > 1 else {
      return "1..."
    }
    
    guard period + 1 < maxPeriod else {
      return "...\(maxPeriod)"
    }
    
    return "..\(period + 1).."
  }
  var body: some View {
    Button(action: {self.showModal.toggle()}) {
      VStack {
        primaryColor(dinFont(Text(self.getPeriodString()), 36))
        primaryColor(dinFont(Text("set period")))
        }.padding().frame(width: width)
        
    }
    .frame(width: width)
    .sheet(isPresented: self.$showModal, onDismiss: {
//      self.settings.period = Int(rs.competition.period - 1)
    }) {
      PeriodModalContent()
        .environmentObject(self.settings)
        .background(UIGlobals.modalSheetBackground)
    }
//    CommonModalButton(imageName: "textformat.123", imageColor: primaryColor , buttonType: .special, text: "set period", onDismiss: {
//    }) {
//      PeriodModalContent().environmentObject(self.settings)
//    }
  }
}

fileprivate struct PeriodModalContent: View {
  
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  @State var nextLocked = false
  @State var period = max(Int(rs.competition.period) - 1, 0)
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Period")
      Spacer()
      CommonPicker(selected: $period, options: Array(1...maxPeriod).map({"\($0)"})).frame(width: width/100*80)
      Spacer()
      VStack(spacing: 0) {
        Divider()
        Button(action: {
          self.settings.setPeriod(self.period + 1)
          self.period += 1
          Vibration.on()
        }) {
          primaryColor(dinFont(Text("next period"))).padding()
        }
          .frame(width: width)
          .disabled(period >= maxPeriod - 1)
      }.opacity(period < maxPeriod - 1 ? 1 : 0)
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(vibrate: false, action: {
          self.settings.setPeriod(self.period)
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
        SetPeriodButtonSwiftUIView().environmentObject(FightSettings())
    }
}
