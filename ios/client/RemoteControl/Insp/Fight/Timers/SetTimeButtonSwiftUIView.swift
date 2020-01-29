//
//  SetTimeButtonSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 28.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import struct NIO.TimeAmount

struct SetTimeButtonSwiftUIView: View {
  var onDismiss: () -> Void = {}
  @State var selectedTime: UInt32 = rs.timer.time
  @EnvironmentObject var settings: FightSettings
  @State var amount = TimeAmount.milliseconds(Int64(rs.timer.time))
  @State var showModal = false
    var body: some View {
      CommonModalButton(imageName: "clock", imageColor: primaryColor , buttonType: .withImage, text: "set time", onDismiss: {
        print("setTime dismiss")
        self.onDismiss()
//        rs.timer.set(time: self.amount, mode: .main)
      }, showModal: $showModal) {
        TimeModalContent(selectedTime: self.$settings.time)
      }
    }
}

func getSelectedTime(_ value: UInt32, divideBy divider: Double, mod: Int = 10) -> Int {
  return Int(Double(value)/divider) % mod
}

fileprivate struct TimeModalContent: View {
  @Environment(\.presentationMode) var presentationMode
  @Binding var selectedTime: UInt32
  @State var minDeci = 0
  @State var minUnit = 0
  @State var secDeci = 0
  @State var secUnit = 0
  
  private let multiplication: [Double] = [10*60, 1*60, 10, 1]
  
  init(selectedTime time: Binding<UInt32>) {
    self._selectedTime = time
    print("time.wrappedValue:")
    print(time.wrappedValue)

    self._minDeci = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[0]*1000))
    self._minUnit = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[1]*1000))
    self._secDeci = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[2]*1000, mod: 6))
    self._secUnit = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[3]*1000))
    print(self.minDeci, self.minUnit, self.secDeci, self.secUnit)
  }
  
  func getSecCount() -> Int64 {
    let selection = [self.minDeci, self.minUnit, self.secDeci, self.secUnit]
    print(selection)
    return selection.enumerated()
      .map { (index, element) in Int64(round(Double(element) * multiplication[index]))}
    .reduce(Int64(0)) {(acc: Int64, value: Int64) in acc + value}
  }
  
  let firstDigitOpts: [String] = Array(0...5).map({"\($0)"})
  let pickerWidth = 19 / 20 * width / 6
  let delimWidth = width / 25
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Time")
      Spacer()
      HStack(spacing: 0){
        Spacer()
        CommonPicker(selected: self.$minDeci).frame(width: pickerWidth)
        CommonPicker(selected: self.$minUnit).frame(width: pickerWidth)
        VStack(spacing: 0){
          Spacer()
          Text(":").foregroundColor(primaryColor)
          Spacer()
        }.frame(width: delimWidth)
        CommonPicker(selected: self.$secDeci, options: firstDigitOpts).frame(width: pickerWidth)
        CommonPicker(selected: self.$secUnit).frame(width: pickerWidth)
        Spacer()
      }
      
      Spacer()
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          let s = self.getSecCount()
          print("getSecCount: \(s)")
          self.selectedTime = UInt32(s * 1000)
          rs.timer.set(time: TimeAmount.seconds(s), mode: .main)
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
    }
  }
}

struct SetTimeButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
      SetTimeButtonSwiftUIView().environmentObject(FightSettings())
    }
}
