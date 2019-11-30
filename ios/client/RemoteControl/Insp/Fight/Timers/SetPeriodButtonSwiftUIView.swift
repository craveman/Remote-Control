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


fileprivate struct PeriodSetter: View {
  @EnvironmentObject var settings: FightSettings
  var body: some View {
    CommonModalButton(imageName: "textformat.123", imageColor: primaryColor , buttonType: .special, text: "set period", onDismiss: {
    }) {
      PeriodModalContent().environmentObject(self.settings)
    }
  }
}

fileprivate struct PeriodModalContent: View {
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  private let maxPeriod: Int = 9
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Period")
      Spacer()
      CommonPicker(selected: self.$settings.period, options: Array(1...maxPeriod).map({"\($0)"})).frame(width: width/100*80)
      Spacer()
      CommonButton(action: {
        self.settings.period += 1
      }, text: "next period", frame: getButtonFrame(.special))
        .opacity(self.settings.period < maxPeriod - 1 ? 1 : 0)
        .disabled(self.settings.period >= maxPeriod - 1)
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
      .border(Color.gray, width: 0.5)
    }
  }
}


struct SetPeriodButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        SetPeriodButtonSwiftUIView()
    }
}
