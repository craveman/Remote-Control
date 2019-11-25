//
//  TimersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 15.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

fileprivate struct TimersSetters: View {
    var body: some View {
        HStack(spacing: 0) {
            CommonButton(action: {

            }, text: "set passive", imageName: "p.square")
            CommonButton(action: {

            }, text: "set time", imageName: "clock")
        }
    }
}


fileprivate struct PeriodSetter: View {
    var body: some View {
        CommonButton(action: {

        }, text: "set period", imageName: "textformat.123", frame: getButtonFrame(.special))
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
