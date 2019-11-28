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
  @Environment(\.presentationMode) var presentationMode
  @State var position: Double = 0//rs.video.player.timestamp
  @State var speed: Double = 10 {
    didSet {
       print("Old value is \(speed), new value is \(oldValue)")
      guard Int(speed * 10) == Int(speed) * 10 else {
         self.speed = Double(Int(speed))
        return
      }
    }
  }
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Video replays")
      Spacer()
//      Text("Hello, World!")
      VStack {
        CommonFloatSlider(sliderValue: $position, minimumValue: 0, maximumvalue: 100, formatter: { _ in "" })
        primaryColor(dinFont(Text("position")))
      }
      VStack {
        CommonFloatSlider(sliderValue: $speed, minimumValue: 0, maximumvalue: 10, formatter: { _ in "" })
        primaryColor(dinFont(Text("speed")))
      }
      
      //        Text("Speed")
      Spacer()
      
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "", imageName: "eject").frame(width: width / 3)
        ConfirmModalButton(action: {
          rs.video.player.pause()
          
        }, text: "", imageName: "pause").frame(width: width / 3)
        ConfirmModalButton(action: {
          rs.video.player.play()
          
        }, text: "", imageName: "play").frame(width: width / 3)
        
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
      
    }
  }
  
  
}

struct VideoButton_Previews: PreviewProvider {
  static var previews: some View {
    VideoButton()
  }
}
