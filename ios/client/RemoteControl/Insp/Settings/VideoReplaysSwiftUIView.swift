//
//  VideoReplaysSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VideoReplaysButtonSwiftUIView: View {
    @State var showModal = false
    var body: some View {
      Button(action: {
        self.showModal.toggle()
      }) {
        VStack {
          ZStack {
            Image(systemName: "square").resizable()
            Image(systemName: "play.fill")
          }.frame(width: 48, height: 48)
          primaryColor(dinFont(Text("video replays"))).fixedSize()
        }
        
        
      }.foregroundColor(primaryColor)
        .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
        .sheet(isPresented: self.$showModal) {
          VideoReplaysSwiftUIView()
      }
    }
}

struct VideoReplaysSwiftUIView: View {
    @Environment(\.presentationMode) var presentationMode
    var body: some View {
        VStack(spacing: 0) {
          CommonModalHeader(title: "Video replays")
          Spacer()
          Text("Hello, World!")
          Spacer()
          Divider()
          HStack {
            ConfirmModalButton(action: {
              self.presentationMode.wrappedValue.dismiss()
            }, color: .green)
          }.padding([.vertical]).frame(width: width)
            
        }
    }
}

struct VideoReplaysSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        VideoReplaysSwiftUIView()
    }
}


struct VideoReplaysButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        VideoReplaysButtonSwiftUIView()
    }
}
