//
//  SetPassiveButtonSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 29.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct SetPassiveButtonSwiftUIView: View {
  var onDismiss: () -> Void = {}
    @State var selectedTime: UInt32 = rs.timer.passive.defaultMilliseconds
    @EnvironmentObject var settings: FightSettings
  
      var body: some View {
        CommonModalButton(imageName: "p.square", imageColor: primaryColor , buttonType: .withImage, text: "set passive", onDismiss: {
          print("setPassive dismiss")
          self.onDismiss()

        }) {
          PassiveModalContent(selectedTime: self.$settings.time).environmentObject(self.settings)
        }
      }
}

fileprivate struct PassiveModalContent: View {
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  @Binding var selectedTime: UInt32
  @State var secCent = 0
  @State var secDeci = 0
  @State var secUnit = 0
  
  
  private let multiplication: [Double] = [100, 10, 1]
  
  init(selectedTime time: Binding<UInt32>) {
    self._selectedTime = time

    self._secCent = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[0]*1000))
    self._secDeci = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[1]*1000))
    self._secUnit = State(wrappedValue: getSelectedTime(time.wrappedValue, divideBy: multiplication[2]*1000))
  }
  
  func getSecCount() -> Int64 {
    let selection = [self.secCent, self.secDeci, self.secUnit]
    print(selection)
    return selection.enumerated()
      .map { (index, element) in Int64(round(Double(element) * multiplication[index]))}
    .reduce(Int64(0)) {(acc: Int64, value: Int64) in acc + value}
  }
  
  let firstDigitOpts: [String] = Array(0...3).map({"\($0)"})
  let pickerWidth = width / 6
  
  func getShowToggleText () -> String {
    return self.settings.showPassive ? "do not show passive" : "show passive";
  }
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Passive")
      Spacer()
      HStack(spacing: 0){
        Spacer()
        CommonPicker(selected: self.$secCent, options: firstDigitOpts).frame(width: pickerWidth)
        CommonPicker(selected: self.$secDeci).frame(width: pickerWidth)
        CommonPicker(selected: self.$secUnit).frame(width: pickerWidth)
        Spacer()
      }
      
      Spacer()
      ShowPassiveToggleButtonSwiftUIView()
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          let s = self.getSecCount()
          print("getSecCount: \(s)")
          self.selectedTime = UInt32(s * 1000)
          rs.timer.passive.defaultMilliseconds = self.selectedTime
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
      .border(Color.gray, width: 0.5)
    }
  }
}

fileprivate struct ShowPassiveToggleButtonSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  
  func getShowToggleText () -> String {
    return self.settings.showPassive ? "do not show passive" : "show passive"
  }
  var body: some View {
      Button(action: {
        self.settings.showPassive.toggle()
        rs.timer.passive.isVisible = self.settings.showPassive
        
      }) {
        primaryColor(dinFont(Text(NSLocalizedString(getShowToggleText(), comment: ""))))
      }
      .frame(width: width).padding([.vertical])
      .border(Color.gray, width: 0.5)
  }
}


struct SetPassiveButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        SetPassiveButtonSwiftUIView()
    }
}
