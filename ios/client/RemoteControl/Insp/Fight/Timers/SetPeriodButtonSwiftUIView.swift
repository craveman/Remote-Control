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
  @State var period = Int(rs.competition.period)
  var body: some View {
    CommonModalButton(imageName: "textformat.123", imageColor: primaryColor , buttonType: .special, text: "set period", onDismiss: {
      rs.competition.period = UInt8(self.period + 1)
    }) {
      PeriodModalContent(period: self.$period)
    }
  }
}

fileprivate struct PeriodModalContent: View {
  @Environment(\.presentationMode) var presentationMode
  @Binding var period: Int
  private let maxPeriod: Int = 9
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Period")
      Spacer()
      CommonPicker(selected: self.$period, options: Array(1...maxPeriod).map({"\($0)"})).frame(width: width/100*80)
      Spacer()
      CommonButton(action: {
        self.period += 1
        rs.competition.period = UInt8(self.period + 1)
      }, text: "next period", frame: getButtonFrame(.special))
        .opacity(period < maxPeriod - 1 ? 1 : 0)
        .disabled(period >= maxPeriod - 1)
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
