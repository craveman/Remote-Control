//
//  SettingsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct SettingsSwiftUIView: View {
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var playback: PlaybackControls
  var body: some View {
    VStack(spacing: 0) {
      HStack(spacing: 0) {
        if (insp.isConnected) {
          DisconnectButtonSwiftUIView()
        }
        if (!insp.isConnected) {
          ConnectButton().environmentObject(insp)
        }
      }
      
      HStack(spacing: 0) {
        PriorityButtonSwiftUIView()
        VideoReplaysButtonSwiftUIView()
          .environmentObject(insp)
          .environmentObject(playback)
      }
      HStack(spacing: 0) {
        NamesSettingsButtonSwiftUIView()
        WeaponsButtonSwiftUIView()
      }
      ScoreButtonSwiftUIView()
    }.frame(width: width, alignment: .top)
  }
}

struct SettingsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    SettingsSwiftUIView()
      .environmentObject(InspSettings())
    .environmentObject(FightSettings())
    .environmentObject(PlaybackControls())
  }
}
