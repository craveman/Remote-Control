//
//  PointsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

let rs = RemoteService.shared

struct PointsSwiftUIView: View {
    @ObservedObject var responder = TimerResponder()
    @Binding var timer: UInt32
    
    func startAction() -> Void {
      rs.timer.start()
      self.responder.start(from: timer)
    }
    func stopAction() -> Void {
      rs.timer.stop()
      self.responder.stop()
      self.timer = responder.milisecondsLeft
    }
    var body: some View {
        VStack(spacing: 0) {
            FightControls()
            MyButtonModalView(timer: timer, countdown: responder.milisecondsLeft, action: startAction, onDismiss: stopAction)
                .padding(50)
        }.frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: .infinity, alignment: .top)
        
    }
}

func getMinutes(_ timer: UInt32) -> String {
    let m = (timer/60000) | 0;
    return "\(m > 9 ? "" : "0")\(m)"
}

func getSeconds(_ timer: UInt32) -> String {
    let s = (timer/1000) % 60 | 0;
    return "\(s > 9 ? "" : "0")\(s)"
}

func getTimeString(_ timer: UInt32) -> String {
    return "\(getMinutes(timer)) : \(getSeconds(timer))";
}

fileprivate struct FightControls: View {
    var pType: PersonType = .none
    @State var leftScore: Int = 0
    @State var rightScore: Int = 0
    var body: some View {
        HStack(spacing: 0) {
            VStack {
                HoldPassiveButton().padding(.vertical, 32)
                    .padding(.horizontal, 50)
                PointsStepper(pType: .left, score: self.$leftScore)
                
            }
            VStack {
                VideoButton()
                    .padding(.vertical, 32)
                    .padding(.horizontal, 50)
                PointsStepper(pType: .right, score: self.$rightScore)
            }
            
        }
        .border(Color.gray)
    }
}

fileprivate struct MyModalView: View {
    @Environment(\.presentationMode) var presentationMode
    var countdown: UInt32
    
    var body: some View {
        
        VStack {
            dinFont(Text("\(getTimeString(countdown))"), 50)
                .padding(CGFloat(50))
                .onTapGesture(count: 1, perform: {
                    self.presentationMode.wrappedValue.dismiss()
                })
        }
    }
}

fileprivate struct MyButtonModalView: View {
    var size = getButtonFrame(.fullWidth)
    @State var showModal = false
    var timer: UInt32
    var countdown: UInt32
    var action: () -> Void
    var onDismiss: () -> Void
    var body: some View {
        Button(action: {
            print("Button Pushed")
            self.action()
            self.showModal = true
        }) {
            dinFont(Text("\(getTimeString(timer))"), 50)
        }
        .frame(width: size.idealWidth, height: size.idealHeight, alignment: size.alignment)
        .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
            MyModalView(countdown: self.countdown)
        }
    }
}

struct PointsSwiftUIView_Previews: PreviewProvider {
    @State static var testTimer = UInt32(1234568)
    static var responder = TimerResponder(milisecondsLeft: testTimer)
    static var previews: some View {
        PointsSwiftUIView(responder: responder, timer: $testTimer)
    }
}
