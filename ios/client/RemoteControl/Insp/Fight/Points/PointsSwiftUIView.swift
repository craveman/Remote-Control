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
  }
  func stopAction() -> Void {
    if rs.timer.state == .running {
      rs.timer.stop()
    }
  }
  var body: some View {
    VStack(spacing: 0) {
      FightControls().border(Color.gray, width: 0.5)
      StartTimerButtonWithModalView(action: startAction, onDismiss: stopAction)
        .padding(50)
      Spacer()
    }
//    .frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: height, alignment: .top)

  }
}

fileprivate struct FightControls: View {
  var pType: PersonType = .none
  var body: some View {
    HStack(spacing: 0) {
      VStack {
        HoldPassiveButton()
        PointsStepper(pType: .left)
      }
      VStack {
        VideoButton()
        PointsStepper(pType: .right)
      }
    }.border(Color.gray)
  }
}

fileprivate struct StartTimerButtonWithModalView: View {
  var size = getButtonFrame(.fullWidth)
  @State var showModal = false
  @EnvironmentObject var settings: FightSettings
  func getStartTimerString() -> String {
    return "\(getTimeString(self.settings.time, true))"
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
    }
    .frame(width: size.idealWidth, height: size.idealHeight, alignment: size.alignment)
    .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
      TimerModalView().environmentObject(self.settings)
    }
  }
}

fileprivate struct TimerModalView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  func getCountdownTimerString() -> String {
    return "\(getTimeString(self.settings.time, true))"
  }
  var body: some View {

    VStack {
      dinFont(Text(getCountdownTimerString()), UIGlobals.timerFontSize)
        .padding(CGFloat(20))
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
    }
  }
}

struct PointsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PointsSwiftUIView()
  }
}
