//
//  PauseSettersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import struct NIO.TimeAmount

let INSPIRATION_MED_TIMOUT = TimeAmount.minutes(5)
let INSPIRATION_SHORT_TIMOUT = TimeAmount.minutes(1)
let INSPIRATION_DEF_TIMOUT = TimeAmount.minutes(3)

var PAUSE_DISSMISED_DEFERED_ACTION_TIMER: Timer? = nil

struct PauseSetters: View {

  @EnvironmentObject var settings: FightSettings
  @State var savedTime: TimeAmount? = nil

  func dismiss () -> Void {
    PAUSE_DISSMISED_DEFERED_ACTION_TIMER?.invalidate()
    rs.timer.stop()
    self.savedTime = nil
  }


  func start (_ time: TimeAmount, _ mode: TimerMode) -> Void {
    PAUSE_DISSMISED_DEFERED_ACTION_TIMER?.invalidate()
    if rs.timer.state == .running {
      rs.timer.stop()
    }
    if rs.timer.mode == .main {
      let time = rs.timer.time
      self.savedTime = .microseconds(Int64(time))
    }

    // todo: update for startTimer(time, mode) when rs will support
    rs.timer.set(time: time, mode: mode)

    PAUSE_DISSMISED_DEFERED_ACTION_TIMER = withDelay({
      rs.timer.start()
    }, 0.25)
  }

  var body: some View {
    HStack(spacing: 0) {
      CommonModalButton(imageName: "plus", imageColor: .red, text: "medical", action: {
        self.start(INSPIRATION_MED_TIMOUT, .medicine)
      }, onDismiss: {
        let time = self.savedTime ?? INSPIRATION_DEF_TIMOUT
        print("PauseSetters::medical:onDismiss")
        self.dismiss()

        PAUSE_DISSMISED_DEFERED_ACTION_TIMER = withDelay({
          rs.timer.set(time: time, mode: .main)
        }, 0.25)
      } ,content: {
        MedicalPauseModalContentUIView(time: self.$settings.time)
      })
      CommonModalButton(imageName: "timer", imageColor: .yellow, text: "1' pause", action: {
        self.start(INSPIRATION_SHORT_TIMOUT, .pause)
      }, onDismiss: {
        print("PauseSetters::1_min_pause:onDismiss")
        self.dismiss()

        PAUSE_DISSMISED_DEFERED_ACTION_TIMER = withDelay({
          rs.competition.period = rs.competition.period + 1
          rs.timer.set(time: INSPIRATION_DEF_TIMOUT, mode: .main)
        }, 0.25)
      } ,content: {
        PauseModalContentUIView(time: self.$settings.time)

      })
    }
  }
}


struct MedicalPauseModalContentUIView: View {
  @Binding var time: UInt32
  @Environment(\.presentationMode) var presentationMode
  var body: some View {
    VStack{
      Spacer()
      dinFont(Text("\(getTimeString(self.time))"), UIGlobals.timerFontSize)
        .foregroundColor(Color.red)
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
      primaryColor(dinFont(Text("medical"), UIGlobals.popupContentFontSize))
        .padding(.top).fixedSize()
      primaryColor(dinFont(Text("pause"), UIGlobals.popupContentFontSize)).fixedSize()
      Spacer()
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }
      .frame(width: width)
      .padding([.vertical])
      .border(Color.gray, width: 0.5)
    }
  }
}

struct PauseModalContentUIView: View {
  @Binding var time: UInt32
  @Environment(\.presentationMode) var presentationMode
  var body: some View {
    VStack{
      Spacer()
      dinFont(Text("\(getTimeString(self.time))"), UIGlobals.timerFontSize)
        .foregroundColor(Color.yellow)
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
      primaryColor(dinFont(Text("pause"), UIGlobals.popupContentFontSize))
        .padding(.top).fixedSize()
      primaryColor(dinFont(Text("1 min"), UIGlobals.popupContentFontSize)).fixedSize()
      Spacer()
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
    }
  }
}

struct PauseSettersSwiftUIView: View {
  var body: some View {
    Text("Hello, World!")

  }
}

struct PauseSettersSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PauseSettersSwiftUIView()
  }
}
