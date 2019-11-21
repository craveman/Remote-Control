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

fileprivate struct TimersSetters: View {
    var body: some View {
        HStack(spacing: 0) {
            TimerButton(action: {
                
            }, text: "set passive", imageName: "p.square")
            TimerButton(action: {
                
            }, text: "set time", imageName: "clock")
        }
    }
}


fileprivate struct PeriodSetter: View {
    var body: some View {
        TimerButton(action: {
            
        }, text: "set period", imageName: "textformat.123", frame: getButtonFrame(.withImageFullWidth))
    }
}

struct TimersSwiftUIView: View {
    var body: some View {
        VStack(spacing: 0){
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
