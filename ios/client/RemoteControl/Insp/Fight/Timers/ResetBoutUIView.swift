//
//  ResetBoutUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI


struct ResetBoutButton: View {
    var body: some View {
        TimerModalButton(imageName: "arrow.2.circlepath", imageColor: nil, buttonType: .withImageFullWidth, text: "reset bout", action: {
            print("ResetBoutButton:action")
        }, onDismiss: {
            print("ResetBoutButton:onDismiss")
        }, content: {
            ResetBoutModalContentUIView()
        })
    }
}


struct ResetBoutModalContentUIView: View {
    @Environment(\.presentationMode) var presentationMode
    var body: some View {
        VStack{
            Spacer()
            Image(systemName: "arrow.2.circlepath").resizable().scaledToFit()
            .frame(width: 200, height: 180).foregroundColor(primaryColor)
            primaryColor(dinFont(Text("reset bout"), UIGlobals.popupContentFontSize))
                .padding(.top)
            Spacer()
            HStack(spacing: 0) {
                ConfirmModalButton(action: {
                    self.presentationMode.wrappedValue.dismiss()
                }, text: "cancel",
                   color: primaryColor, imageName: "multiply")
                    .frame(width: width/2)
                    .padding(.top).padding(.bottom)
                    .border(Color.gray, width: 0.5)
                ConfirmModalButton(action: {
                    rs.competition.reset()
                    self.presentationMode.wrappedValue.dismiss()
                }, text: "confirm", color: .green)
                    .frame(width: width/2)
                    .padding(.top).padding(.bottom)
                    .border(Color.gray, width: 0.5)
            }.border(Color.gray, width: 0.5)
        }
    }
}

struct ResetBoutUIView: View {
    var body: some View {
        ResetBoutModalContentUIView()
    }
}

struct ResetBoutUIView_Previews: PreviewProvider {
    static var previews: some View {
        ResetBoutUIView()
    }
}
