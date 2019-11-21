//
//  TimersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

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
        HStack {
            TimerModalButton(text: "medical", action: {
                self.start(.medicine, 5 * 60 * 1000)
            }, onDismiss: {
                print("PauseSetters::medical:onDismiss")
                self.dismiss()
            } ,content: {
                MedicalPauseModalContentUIView(time: self.$timer)
                
            })
            TimerModalButton(text: "1' pause", action: {
                self.start(.pause, 1 * 60 * 1000)
            }, onDismiss: {
                print("PauseSetters::1_min_pause:onDismiss")
                self.dismiss()
            } ,content: {
                PauseModalContentUIView(time: self.$timer)
                
            })
        }
    }
}

fileprivate struct DoneModalAction: View {
    var action: () -> Void = {}
    var body: some View {
        Button(action: {
            self.action()
        }) {
            VStack{
                Image(systemName: "checkmark")
                    .imageScale(.large)
                    .foregroundColor(.green)
                    .padding()
                primaryColor(dinFont(Text("done"), UIGlobals.appSmallerFontSize))
            }.padding(.all, 24)
        }
        
    }
}

fileprivate struct MedicalPauseModalContentUIView: View {
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
                .padding(.top)
            primaryColor(dinFont(Text("pause"), UIGlobals.popupContentFontSize))
            Spacer()
            DoneModalAction(action: {
                self.presentationMode.wrappedValue.dismiss()
            })
        }
    }
}

fileprivate struct PauseModalContentUIView: View {
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
                .padding(.top)
            primaryColor(dinFont(Text("1 min"), UIGlobals.popupContentFontSize))
            Spacer()
            DoneModalAction(action: {
                self.presentationMode.wrappedValue.dismiss()
            })
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
