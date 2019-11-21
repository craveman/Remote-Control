//
//  TimersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

let rs = RemoteService.shared


fileprivate struct ResetBoutButton: View {
    var frame = getButtonFrame(.fullWidth)
    var body: some View {
        TimerButton(action: {rs.competition.reset()}, text: "reset bout", imageName: "", frame: getButtonFrame(.fullWidth))
    }
}


fileprivate struct TimerButton: View {
    var action: () -> Void
    var text = "Button"
    var imageName = ""
    var frame = getButtonFrame(.withImage)
    
    var body: some View {
        Button(action: {
            self.action()
        }) {
            VStack{
                if imageName.count > 0 {
                    Image(imageName).renderingMode(.original)
                }
                primaryColor(dinFont(Text(self.text)))
            }
        }.frame(width: frame.idealWidth, height: frame.idealHeight, alignment: frame.alignment)
    }
}

fileprivate struct TimerModalButton<Content>: View where Content: View {
    @State var showModal = false
    let content: () -> Content
    var text = "Button"
    var imageName = ""
    var frame = getButtonFrame(.withImage)
    var action: () -> Void = {}
    var onDismiss: () -> Void = {}
    
    
    init(text: String, action: @escaping () -> Void = {}, onDismiss: @escaping () -> Void = {}, @ViewBuilder content: @escaping () -> Content) {
    
        self.text = text
        self.action = action
        self.onDismiss = onDismiss
        self.content = content
    }
    
    var body: some View {
        TimerButton(action: {
            self.showModal.toggle()
            self.action()
            
        }, text: self.text, imageName: self.imageName, frame: self.frame)
            .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
                self.content()
        }
    }
}

fileprivate struct TimersSetters: View {
    var body: some View {
        HStack {
            TimerButton(action: {
                
            }, text: "set passive")
            TimerButton(action: {
                
            }, text: "set time")
        }
    }
}


fileprivate struct PauseSetters: View {
    @State var timer: UInt32 = 0
    @State var subId: UUID? = nil
    var body: some View {
        HStack {
            TimerModalButton(text: "medical", action: {
                rs.timer.stop()
                self.subId = rs.timer.timeProperty.on(change: { next in
                    self.timer = next
                })
                rs.timer.time = 5 * 60000
                rs.timer.mode = .medicine
                rs.timer.start()
            }, onDismiss: {
                print("PauseSetters::medical:onDismiss")
                if let id = self.subId {
                    rs.timer.timeProperty.remove(observer: id)
                }
            } ,content: {
                MedicalPauseModalContentUIView(time: self.$timer)
                
            })
            TimerModalButton(text: "1' pause", action: {
                rs.timer.stop()
                self.subId = rs.timer.timeProperty.on(change: { next in
                    self.timer = next
                })
                rs.timer.time = 60000
                rs.timer.mode = .pause
                rs.timer.start()
            }, onDismiss: {
                print("PauseSetters::1_min_pause:onDismiss")
                if let id = self.subId {
                    rs.timer.timeProperty.remove(observer: id)
                }
            } ,content: {
                PauseModalContentUIView(time: self.$timer)
                
            })
        }
    }
}

fileprivate struct MedicalPauseModalContentUIView: View {
    @Binding var time: UInt32
    
    var body: some View {
        VStack{
            dinFont(Text("\(getTimeString(self.time))"), UIGlobals.timerFontSize)
                .foregroundColor(Color.red)
            primaryColor(dinFont(Text("medical"), UIGlobals.popupContentFontSize))
                .padding(.top)
            primaryColor(dinFont(Text("pause"), UIGlobals.popupContentFontSize))
        }
    }
}


fileprivate struct PauseModalContentUIView: View {
    @Binding var time: UInt32
    
    var body: some View {
        VStack{
            dinFont(Text("\(getTimeString(self.time))"), UIGlobals.timerFontSize)
                .foregroundColor(Color.yellow)
            primaryColor(dinFont(Text("pause"), UIGlobals.popupContentFontSize))
                .padding(.top)
            primaryColor(dinFont(Text("1 min"), UIGlobals.popupContentFontSize))
        }
    }
}

fileprivate struct PeriodSetter: View {
    var body: some View {
        TimerButton(action: {
            
        }, text: "set period", frame: getButtonFrame(.fullWidth))
    }
}

struct TimersSwiftUIView: View {
    var body: some View {
        VStack{
            ResetBoutButton()
            PauseSetters()
            TimersSetters()
            PeriodSetter()
        }
            .frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: .infinity, alignment: .top)
        
    }
}

struct TimersSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        TimersSwiftUIView()
    }
}
