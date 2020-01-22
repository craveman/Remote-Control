//
//  PauseSettersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import struct NIO.TimeAmount

let SYNC_SM02_DELAY = 0.25
struct PauseSetters: View {
  
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  
  private func dismiss () -> Void {
    self.settings.PAUSE_DISSMISED_DEFERED_ACTION_TIMER?.invalidate()
    rs.timer.stop()
    self.settings.savedTime = nil
    Vibration.on()
  }
  
  private func start (_ time: TimeAmount, _ mode: TimerMode) -> Void {
    self.settings.PAUSE_DISSMISED_DEFERED_ACTION_TIMER?.invalidate()
    if rs.timer.mode == .main {
      let time = rs.timer.time
      self.settings.savedTime = .milliseconds(Int64(time))
      print("set savedTime", self.settings.savedTime!)
    }
    
    // todo: update for startTimer(time, mode) when rs will support
    rs.timer.start(time, mode: mode)
  }
  
  func pauseDismissAction() -> Void {
    unsubscribeFinish()
    self.dismiss()
    self.settings.period += 1
  }
  
  func medicalDismissAction() -> Void {
    unsubscribeFinish()
    let time = self.settings.savedTime ?? INSPIRATION_DEF_TIMOUT
    print("set savedTime", time)
    self.dismiss()
    rs.timer.set(time: time, mode: .main)
  }
  
  func unsubscribeFinish() -> Void {
    guard let uuid = self.settings.PAUSE_FINISHED_LISTENER_ID else {
      return
    }
    rs.timer.isPauseFinishedProperty.remove(observer: uuid)
  }
  
  func subscribeFinished() {
    self.unsubscribeFinish()
    self.settings.PAUSE_FINISHED_LISTENER_ID = rs.timer.isPauseFinishedProperty.on(change: { isFinished in
      if (isFinished) {
        DispatchQueue.main.async {
          self.insp.shouldShowPauseView = false
          self.insp.shouldShowMedicalView = false
        }
        Vibration.on()
      }
    })
  }
  
  func medicalAction() {
    DispatchQueue.main.async {
      self.insp.shouldShowMedicalView = true
      self.start(INSPIRATION_MED_TIMOUT, .medicine)
      self.subscribeFinished()
    }
    Vibration.on()
  }
  
  
  func pauseAction() {
    DispatchQueue.main.async {
      self.insp.shouldShowPauseView = true
      self.start(INSPIRATION_SHORT_TIMOUT, .pause)
      self.subscribeFinished()
    }
    Vibration.on()
  }
  
  var body: some View {
    HStack(spacing: 0) {
      CommonModalButton(imageName: "plus", imageColor: .red, text: "button medical", action: self.medicalAction, onDismiss: self.medicalDismissAction, showModal: self.$insp.shouldShowMedicalView){
        MedicalPauseModalContentUIView(time: self.$settings.time)
      }
      CommonModalButton(imageName: "timer", imageColor: .yellow, text: "button 1' pause", action: self.pauseAction, onDismiss: self.pauseDismissAction, showModal: self.$insp.shouldShowPauseView){
        PauseModalContentUIView(time: self.$settings.time)
      }
    }
  }
}


struct MedicalPauseModalContentUIView: View {
  @Binding var time: UInt32
  @Environment(\.presentationMode) var presentationMode
  
  func getLabel() -> String {
    let label = self.time > 0 ? "Stop": "Close"
    return NSLocalizedString(label, comment: "")
  }
  
  var body: some View {
    VStack{
      Spacer()
      dinFont(Text(getLabel()), UIGlobals.timerFontSize)
        .foregroundColor(Color.red)
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
      primaryColor(dinFont(Text("medical pause 1"), UIGlobals.popupContentFontSize))
        .padding(.top).fixedSize()
      primaryColor(dinFont(Text("medical pause 2"), UIGlobals.popupContentFontSize)).fixedSize()
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }
      .padding([.vertical])
      .frame(width: width)
    }
  }
}

struct PauseModalContentUIView: View {
  @Binding var time: UInt32
  @Environment(\.presentationMode) var presentationMode
  
  func getLabel() -> String {
    let label = self.time > 0 ? "Stop": "Close"
    return NSLocalizedString(label, comment: "")
  }
  
  var body: some View {
    VStack{
      Spacer()
      dinFont(Text(getLabel()), UIGlobals.timerFontSize)
        .foregroundColor(Color.yellow)
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
      primaryColor(dinFont(Text("1' pause 1"), UIGlobals.popupContentFontSize))
        .padding(.top).fixedSize()
      primaryColor(dinFont(Text("1' pause 2"), UIGlobals.popupContentFontSize)).fixedSize()
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.padding([.vertical]).frame(width: width)
    }
  }
}
