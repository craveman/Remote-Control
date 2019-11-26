//
//  NamesSettingsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct NamesSettingsButtonSwiftUIView: View {
   @State var showModal = false
    var body: some View {
      Button(action: {
        self.showModal.toggle()
      }) {
        VStack {
          primaryColor(dinFont(Text("John..."), 48)).fixedSize()
          primaryColor(dinFont(Text("names")))
        }
        
        
      }.foregroundColor(primaryColor)
        .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
        .sheet(isPresented: self.$showModal) {
          NamesSettingsSwiftUIView()
      }
    }
}


struct NamesSettingsSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
    var body: some View {
        VStack(spacing: 0) {
          CommonModalHeader(title: "Set names")
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

struct NamesSettingsSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        NamesSettingsSwiftUIView()
    }
}



struct NamesSettingsButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        NamesSettingsButtonSwiftUIView()
    }
}
