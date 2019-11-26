//
//  ScoreSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI



struct ScoreButtonSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        primaryColor(dinFont(Text("\(self.settings.leftScore):\(self.settings.rightScore)"), 48))
        primaryColor(dinFont(Text("score")))
      }
      
      
    }.foregroundColor(primaryColor)
      .frame(width: width, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
      .sheet(isPresented: self.$showModal) {
        ScoreSwiftUIView()
    }
  }
}


struct ScoreSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Set score")
      Spacer()
      Text("Hello, World!")
      Spacer()
      
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
    }
  }
}

struct ScoreSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    ScoreSwiftUIView()
  }
}


struct ScoreButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    ScoreButtonSwiftUIView()
  }
}
