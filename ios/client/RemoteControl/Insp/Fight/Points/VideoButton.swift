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
  @EnvironmentObject var playback: PlaybackControls
  @State var showModal = false
  var onDismiss: () -> Void = {
    print("view video dissmissed")
  }
  var frame = getButtonFrame(.fullWidth)
  var body: some View {
    Button(action: {
      print("View video")
      self.showModal = !self.showModal
    }){ primaryColor(dinFont(Text("view video"))) }
      .frame(width: frame.idealWidth, height: frame.idealHeight, alignment: .center)
      .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
        Group() {
          CommonModalHeader(title: "Replay RC")
          VideoRC({
            self.showModal = !self.showModal
             })
            .background(UIGlobals.modalSheetBackground)
            .environmentObject(self.playback)
        }
        
    }
  }
}


class SliderBindingManager: ObservableObject {
  init(_ initialValue: Double, updater setter: @escaping (Double, Double) -> Void) {
    self.setter = setter
    self.value = initialValue
  }
  private var setter: (Double, Double) -> Void = {(_, _) in}
  @Published var value: Double = 0 {
    didSet {
      self.setter(value, oldValue)
    }
  }
  
  func onChange(updater setter: @escaping (Double, Double) -> Void) -> Void {
    self.setter = setter
  }
}

public struct VideoRC: View {
  @EnvironmentObject var playback: PlaybackControls
  
  var ejectAction: () -> Void
  
  init(_ ejectAction: @escaping () -> Void) {
    self.ejectAction = ejectAction
  }
  //rs.video.player.timestamp
  var sliders: some View {
    Group() {
      VStack {
        CommonFloatSlider(sliderValue: $playback.currentPosition, minimumValue: 0, maximumvalue: 100, formatter: { _ in "" }).disabled(playback.isPlayActive)
        primaryColor(dinFont(Text("position")))
      }
      VStack {
        CommonFloatSlider(sliderValue: $playback.selectedSpeed, minimumValue: 0, maximumvalue: 10, formatter: { _ in "" })
        primaryColor(dinFont(Text("speed")))
      }
    }
  }
  
  var playPauseButton: some View {
    Group() {
      if self.playback.isPlayActive {
        ConfirmModalButton(action: {
          self.playback.isPlayActive = false
          
        }, text: "", imageName: "pause").frame(width: width / 2)
      }
      if !self.playback.isPlayActive {
        ConfirmModalButton(action: {
          self.playback.isPlayActive = true
          
        }, text: "", imageName: "play").frame(width: width / 2)
      }
    }
  }
  
  public var body: some View {
    VStack(spacing: 0) {
      Spacer()
      sliders
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(action: {self.ejectAction()}, text: "", imageName: "eject").frame(width: width / 2)
        playPauseButton
        
      }
      
      
    }
  }
  
  
}

struct VideoButton_Previews: PreviewProvider {
  static var previews: some View {
    VideoButton().environmentObject(PlaybackControls())
  }
}


struct VideoRC_Previews: PreviewProvider {
  static var previews: some View {
    VideoRC({}).environmentObject(PlaybackControls())
  }
}
