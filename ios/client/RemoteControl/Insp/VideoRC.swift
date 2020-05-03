//
//  VideoRC.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 18.04.2020.
//  Copyright © 2020 Sergei Andreev. All rights reserved.
//

import SwiftUI

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

struct VideoRC: View {
  @EnvironmentObject var playback: PlaybackControls
  
  var ejectAction: () -> Void
  
  init(_ ejectAction: @escaping () -> Void) {
    self.ejectAction = ejectAction
  }
  //rs.video.player.timestamp
  var sliders: some View {
    VStack() {
      VStack(spacing: 0) {
        primaryColor(dinFont(Text("position")))
        CommonFloatSlider(sliderValue: $playback.currentPosition, minimumValue: 0, maximumvalue: 100, formatter: { _ in "" })
        
      }.disabled(playback.isPlayActive)
      Spacer()
      VStack(spacing: 0) {
        primaryColor(dinFont(Text("speed")))
        CommonFloatSlider(sliderValue: $playback.selectedSpeed, minimumValue: 1, maximumvalue: 10, formatter: { _ in "" }, onComplete: {
          self.playback.selectedSpeed = self.$playback.selectedSpeed.wrappedValue.rounded()
          Vibration.on()
        })
      }
    }.frame(height: 240)
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
        ConfirmModalButton(vibrate: false, action: {
          self.playback.eject()
          self.ejectAction()
        }, text: "", imageName: "eject").frame(width: width / 2)
        playPauseButton
        
      }
      
      
    }
  }
  
  
}

struct VideoRC_Previews: PreviewProvider {
  static var previews: some View {
    VideoRC({}).environmentObject(PlaybackControls())
  }
}

