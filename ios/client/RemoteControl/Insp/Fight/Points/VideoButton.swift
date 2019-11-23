//
//  SwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VideoButton: View {
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  var onDismiss: () -> Void = {
    print("view video dissmissed")
  }
    var frame = getButtonFrame(.basic)
    var body: some View {
        Button(action: {
            print("View video")
          self.showModal = !self.showModal;
          }){ primaryColor(dinFont(Text("view video"))) }
          .frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center)
          .border(Color.gray, width: 0.5)
          .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
            VideoRC()
      }
    }
}

fileprivate struct VideoRC: View {
  var body: some View {
    VStack(){
      Text("Position")
      
      Text("Speed")
      
      HStack{
        Button(action: {
          rs.video.player.pause()
        }) {
          Text("Pause")
        }
        Button(action: {
          rs.video.player.play()
        }) {
          Text("Play")
        }
      }
    }
    
  }
}

struct VideoButton_Previews: PreviewProvider {
    static var previews: some View {
        VideoButton()
    }
}
