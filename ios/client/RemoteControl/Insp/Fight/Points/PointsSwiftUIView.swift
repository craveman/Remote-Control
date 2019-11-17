//
//  PointsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

final class TimerResponder: ObservableObject {
    @Published private(set) var finished: Bool = false
    @Published private(set) var tick: UInt32 = 0
    @Published private(set) var milisecondsLeft: UInt32 = 0
    private var emulationInterval: Timer = Timer()
    private(set) var interval: Double = 0.01
    private(set) var step: UInt32 = 10
    init(milisecondsLeft: UInt32 = 1, step: UInt32 = UInt32(1000)) {
        self.interval = Double(Int(step) / 1000);
        self.step = step
    }
    
    public func start(_ ms: UInt32) {
        self.milisecondsLeft = ms
        self.finished = false
        self.emulationInterval = Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(fireTimer), userInfo: nil, repeats: true)
    }
    
    public func stop() {
        self.finished.toggle()
        emulationInterval.invalidate()
    }
    
    @objc func fireTimer() -> Void {
        self.tick += 1;
        if (milisecondsLeft <= UInt32(interval)) {
            milisecondsLeft = UInt32(0)
            self.stop()
            return
        }
        milisecondsLeft -= step
    }
}

struct PointsSwiftUIView: View {
    @ObservedObject var responder = TimerResponder()
    @Binding var timer: UInt32
    
    func startAction() -> Void {
        self.responder.start(timer)
    }
    func stopAction() -> Void {
        self.responder.stop()
        self.timer = responder.milisecondsLeft
    }
    var body: some View {
        VStack {
            FightControls()
            HStack {
                MyButtonModalView(timer: responder.milisecondsLeft, action: startAction, onDismiss: stopAction)
                    .padding(CGFloat(50))
            }
            
        }.frame(minWidth: 400, idealWidth: .infinity, maxWidth: .infinity, minHeight: 400, idealHeight: .infinity, maxHeight: .infinity, alignment: .top)
       
    }
}

fileprivate struct FightControls: View {
    var body: some View {
        HStack {
            VStack {
                HoldPassiveButton().padding(.vertical, 32)
                    .padding(.horizontal, 50)
                PointsStepper()
                
            }
            VStack {
                VideoButton()
                .padding(.vertical, 32)
                .padding(.horizontal, 50)
                PointsStepper()
            }
            
           
        }.padding(.all, 0).border(Color.gray)
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

fileprivate struct MyModalView: View {
    @Environment(\.presentationMode) var presentationMode
    var timer: UInt32
        
    var body: some View {
        
        VStack {
            dinFont(Text("\(getTimeString(timer))"), 50)
            .padding(CGFloat(50))
            .onTapGesture(count: 1, perform: {
                self.presentationMode.wrappedValue.dismiss()
            })
            }
    }
}

fileprivate struct MyButtonModalView: View {
    @State var showModal = false
    var timer: UInt32
    var action: () -> Void
    var onDismiss: () -> Void
    var body: some View {
        Button(action: {
            print("Button Pushed")
            self.action()
            self.showModal = true
        }) {
            dinFont(Text("\(getTimeString(timer))"), 50)
        }.sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
            MyModalView(timer: self.timer)
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
