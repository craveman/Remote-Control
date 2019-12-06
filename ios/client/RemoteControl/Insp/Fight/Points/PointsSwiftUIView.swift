//
//  PointsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct PointsSwiftUIView: View {
  
  func startAction() -> Void {
    rs.timer.start()
    Vibration.on()
  }
  func stopAction() -> Void {
    
    if rs.timer.state == .running {
      rs.timer.stop()
    }
    Vibration.on()
  }
  var body: some View {
    VStack(spacing: 0) {
      FightControls()
      StartTimerButtonWithModalView(action: startAction, onDismiss: stopAction)
        
      Spacer()
    }
    //    .frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: height, alignment: .top)
    
  }
}

fileprivate struct FightControls: View {
  var pType: PersonType = .none
  var body: some View {
    VStack(spacing: 0) {
      Divider()
      SetPeriodButtonSwiftUIView()
      Divider()
      HStack(spacing: 0) {
        PointsStepper(pType: .left)
        PointsStepper(pType: .right)
      }
    }
    
  }
}

fileprivate struct StartTimerButtonWithModalView: View {
  var size = getButtonFrame(.fullWidth)
  @State var showModal = false
  @EnvironmentObject var settings: FightSettings
  func getStartTimerString() -> String {
    return NSLocalizedString("Start", comment: "")
    //    return "\(getTimeString(self.settings.time, true))"
  }
  
  var action: () -> Void
  var onDismiss: () -> Void
  var body: some View {
    Button(action: {
      print("Button Pushed")
      self.action()
      self.showModal = true
    }) {
      primaryColor(dinFont(Text(getStartTimerString()), UIGlobals.timerFontSize))
      .padding(20)
      .frame(width: width)
    }.frame(width: width)
    .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
      TimerModalView().environmentObject(self.settings)
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
  var body: some View {
    
    VStack {
      dinFont(Text(getCountdownTimerString()), UIGlobals.timerFontSize)
        .padding(20)
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
    }
  }
}

struct PointsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PointsSwiftUIView().environmentObject(FightSettings())
  }
}
