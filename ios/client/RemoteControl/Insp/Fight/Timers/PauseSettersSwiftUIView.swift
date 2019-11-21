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

struct PauseSetters: View {
    @State var timer: UInt32 = 0
    @State var subId: UUID? = nil
    @State var savedTime: UInt32? = nil
    
    func dismiss() -> Void {
        rs.timer.stop()
        if let id = self.subId {
            rs.timer.timeProperty.remove(observer: id)
        }
        if let ms = self.savedTime {
            rs.timer.time = ms
            rs.timer.mode = .main
            self.savedTime = nil
        }
    }
    
    
    func start(_ mode: TimerMode, _ ms: UInt32) -> Void {
        if rs.timer.mode == .main {
            self.savedTime = rs.timer.time
        }
        self.subId = rs.timer.timeProperty.on(change: { next in
            self.timer = next
        })
        rs.timer.time = ms
        rs.timer.mode = mode
        rs.timer.start()
    }
    var body: some View {
        HStack(spacing: 0) {
            TimerModalButton(imageName: "plus", imageColor: .red, text: "medical", action: {
                self.start(.medicine, INSPIRATION_MED_TIMOUT)
            }, onDismiss: {
                print("PauseSetters::medical:onDismiss")
                self.dismiss()
            } ,content: {
                MedicalPauseModalContentUIView(time: self.$timer)
                
            })
            TimerModalButton(imageName: "timer", imageColor: .yellow, text: "1' pause", action: {
                self.start(.pause, INSPIRATION_SHORT_TIMOUT)
            }, onDismiss: {
                print("PauseSetters::1_min_pause:onDismiss")
                self.dismiss()
            } ,content: {
                PauseModalContentUIView(time: self.$timer)
                
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
