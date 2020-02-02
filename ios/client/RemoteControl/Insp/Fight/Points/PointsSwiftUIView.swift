//
//  PointsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Foundation

struct PointsSwiftUIView: View {
  
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var settings: FightSettings
  
  func startAction() -> Void {
    if(rs.timer.state == .suspended) {
      rs.timer.start()
      Vibration.on()
    }
    self.insp.shouldShowTimerView = true
  }
  func stopAction() -> Void {
    if(rs.timer.state == .running) {
      rs.timer.stop()
      Vibration.on()
    }
  }
  var body: some View {
    VStack(spacing: 0) {
      Divider()
      Spacer()
      FightControls()
      StartTimerButtonWithModalView(showModal: $insp.shouldShowTimerView ,start: startAction, stop: stopAction)
      Spacer()
    }
  }
}

fileprivate struct FightControls: View {
  var pType: PersonType = .none
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        PointsStepper(pType: .left)
        PointsStepper(pType: .right)
      }
    }
    
  }
}

fileprivate struct StartTimerButtonWithModalView: View {
  var size = getButtonFrame(.fullWidth)
  @Binding var showModal: Bool
  @EnvironmentObject var settings: FightSettings
  
  func getStartTimerString() -> String {
    return NSLocalizedString("Start", comment: "")
    //    return "\(getTimeString(self.settings.time, true))"
  }
  
  var start: () -> Void
  var stop: () -> Void
  
  var body: some View {
    Button(action: {
      print("Start Button Pushed")
      self.start()
      //      self.showModal = true
    }) {
      primaryColor(dinFont(Text(getStartTimerString()), UIGlobals.timerFontSize))
        .padding(20)
        .frame(width: width)
    }.frame(width: width)
      .sheet(isPresented: self.$showModal, onDismiss: {
        self.stop()
        if rs.timer.time == 0 {
          Vibration.on()
        }
      }) {
        TimerModalView(onTap: self.stop)
          .environmentObject(self.settings)
          .background(UIGlobals.modalSheetBackground)
    }
  }
}

fileprivate struct TimerModalView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  func getCountdownTimerString() -> String {
    //    return "\(getTimeString(self.settings.time, true))"
    let label = self.settings.time > 0 ? "Stop" : "Close"
    return NSLocalizedString(label, comment: "")
  }
  var onTap: () -> Void = {}
  var body: some View {
    
    VStack {
      Spacer()
      dinFont(Text(getCountdownTimerString()), UIGlobals.timerFontSize)
        .frame(width: 4 * width / 5, height: 4 * height / 5)
//        .background(Color.red)
        .padding()
        .onTapGesture(count: 1, perform: {
          self.onTap()
          self.presentationMode.wrappedValue.dismiss()
        })
      Spacer()
    }.frame(width: width)
  }
}

struct PointsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PointsSwiftUIView()
      .environmentObject(FightSettings())
      .environmentObject(InspSettings())
  }
}
