//
//  SwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

// NOT USED
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

struct VideoButton_Previews: PreviewProvider {
  static var previews: some View {
    VideoButton().environmentObject(PlaybackControls())
  }
}

