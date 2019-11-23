//
//  PauseSettersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

let INSPIRATION_MED_TIMOUT = UInt32(5 * 60 * 1000)
let INSPIRATION_SHORT_TIMOUT = UInt32(1 * 60 * 1000)
let INSPIRATION_DEF_TIMOUT = UInt32(3 * 60 * 1000)

struct PauseSetters: View {
  @EnvironmentObject var settings: FightSettings
  @State var savedTime: UInt32? = nil
  
  func dismiss() -> Void {
    rs.timer.stop()
    self.savedTime = nil
  }
  
  
  func start(_ mode: TimerMode, _ ms: UInt32) -> Void {
    if rs.timer.mode == .main {
      self.savedTime = rs.timer.time
    }
    rs.timer.mode = mode
    withDelay({
      rs.timer.time = ms
      withDelay({
        rs.timer.start()
      })
    })
  }
  var body: some View {
    HStack(spacing: 0) {
      CommonModalButton(imageName: "plus", imageColor: .red, text: "medical", action: {
        self.start(.medicine, INSPIRATION_MED_TIMOUT)
      }, onDismiss: {
        let ms = self.savedTime ?? INSPIRATION_DEF_TIMOUT
        print("PauseSetters::medical:onDismiss")
        self.dismiss()
        
        withDelay({
          rs.timer.mode = .main
          withDelay({
            rs.timer.time = ms
          })
        })
      } ,content: {
        MedicalPauseModalContentUIView(time: self.$settings.time)
        
      })
      CommonModalButton(imageName: "timer", imageColor: .yellow, text: "1' pause", action: {
        self.start(.pause, INSPIRATION_SHORT_TIMOUT)
      }, onDismiss: {
        print("PauseSetters::1_min_pause:onDismiss")
        self.dismiss()
        
        withDelay({
          rs.timer.mode = .main
          rs.competition.period = rs.competition.period + 1
          rs.timer.time = INSPIRATION_DEF_TIMOUT
        })
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
      }.frame(width: width).padding(.top).padding(.bottom)
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
      }.frame(width: width).padding(.top).padding(.bottom)
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
