//
//  SetTimeButtonSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 28.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import struct NIO.TimeAmount

fileprivate func log(_ items: Any...) {
  print("SetTimeButtonSwiftUIView:log: ", items)
}

struct SetTimeButtonSwiftUIView: View {
  var onDismiss: () -> Void = {}
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
    var body: some View {
      CommonModalButton(imageName: "clock", imageColor: primaryColor , buttonType: .withImage, text: "set time", onDismiss: {
        log("setTime dismiss")
        self.onDismiss()
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
  let minDeciMax = 3
  @State var minDeci = 0
  @State var minUnit = 0
  @State var secDeci = 0
  @State var secUnit = 0
  
  private let multiplication: [Double] = [10*60, 1*60, 10, 1]
  
  init(selectedTime time: Binding<UInt32>) {
    self.minFirstDigitOpts = Array(0...minDeciMax).map({"\($0)"})
    self.secFirstDigitOpts = Array(0...5).map({"\($0)"})
    self._selectedTime = time
    let ms = min(time.wrappedValue, UInt32(minDeciMax * Int(multiplication[0])*1000)) + 1
    log("time.wrappedValue:", time.wrappedValue, "ms: ", ms)

    self._minDeci = State(wrappedValue: getSelectedTime(ms, divideBy: multiplication[0]*1000))
    self._minUnit = State(wrappedValue: getSelectedTime(ms, divideBy: multiplication[1]*1000))
    self._secDeci = State(wrappedValue: getSelectedTime(ms, divideBy: multiplication[2]*1000, mod: 6))
    self._secUnit = State(wrappedValue: getSelectedTime(ms, divideBy: multiplication[3]*1000))
    
    log("init", self.minDeci, self.minUnit, self.secDeci, self.secUnit)
  }
  
  func getSecCount() -> Int64 {
    let selection = self.minDeci < minDeciMax ? [self.minDeci, self.minUnit, self.secDeci, self.secUnit] : [minDeciMax, 0, 0, 0]
    log("getSecCount", selection)
    return selection.enumerated()
      .map { (index, element) in Int64(round(Double(element) * multiplication[index]))}
    .reduce(Int64(0)) {(acc: Int64, value: Int64) in acc + value}
  }

  let minFirstDigitOpts: [String]
  let secFirstDigitOpts: [String]
  let zeroDigitOpts: [String] = ["0"]
  let pickerWidth = 19 / 20 * width / 6
  let delimWidth = width / 25
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Time")
      Spacer()
      HStack(spacing: 0){
        Spacer()
        CommonPicker(selected: $minDeci, options: minFirstDigitOpts).frame(width: pickerWidth)
        VStack(spacing: 0) {
          if (minDeci < minDeciMax) {
            CommonPicker(selected: $minUnit).frame(width: pickerWidth)
          }
          if (minDeci == minDeciMax) {
            CommonPicker(selected: $minUnit, options: zeroDigitOpts).frame(width: pickerWidth)
          }
        }
        VStack(spacing: 0){
          Spacer()
          Text(":").foregroundColor(primaryColor)
          Spacer()
        }.frame(width: delimWidth)
        VStack(spacing: 0) {
          if (minDeci < minDeciMax) {
            CommonPicker(selected: $secDeci, options: secFirstDigitOpts).frame(width: pickerWidth)
          }
          if (minDeci == minDeciMax) {
            CommonPicker(selected: $secDeci, options: zeroDigitOpts).frame(width: pickerWidth)
          }
        }
        VStack(spacing: 0) {
          if (minDeci < minDeciMax) {
            CommonPicker(selected: $secUnit).frame(width: pickerWidth)
          }
          if (minDeci == minDeciMax) {
            CommonPicker(selected: $secUnit, options: zeroDigitOpts).frame(width: pickerWidth)
          }
        }
        Spacer()
      }
      
      Spacer()
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          let s = self.getSecCount()
          log("getSecCount: \(s)")
          
          rs.timer.set(time: TimeAmount.seconds(s), mode: .main)
          withDelay({
            self.selectedTime = UInt32(s * 1000)
          }, RemoteService.SYNC_INTERVAL)
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
    }
  }
}

struct ZeroablePicker: View {
  @Binding var value: Int
  @Binding var toggle: Bool
  
  var width: CGFloat
  var options: [String] = Array(0...9).map({"\($0)"})
  let zeroDigitOpts: [String] = ["0"]
  
  var body: some View {
    VStack(spacing: 0) {
      if (!toggle) {
        CommonPicker(selected: self.$value, options: options).frame(width: width)
      }
      if (toggle) {
        CommonPicker(selected: self.$value, options: zeroDigitOpts).frame(width: width)
      }
    }
  }
}

struct SetTimeButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
      SetTimeButtonSwiftUIView().environmentObject(FightSettings())
    }
}
