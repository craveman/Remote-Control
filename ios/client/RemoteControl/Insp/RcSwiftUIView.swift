//
//  RcSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct RcSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var playback: PlaybackControls
  let animationTime = 0.25
  var animation: Animation {
    Animation.easeIn(duration: animationTime)
  }
  
  @State var ofc: CGFloat = -height
  
  var general: some View {
    VStack(spacing: 0){
      if !insp.isConnected {
        ZStack() {
          primaryColor(dinFont(Text("disconnected"), UIGlobals.appSmallerFontSize))
            .frame(width: width)
            .background(Color.red)
        }
      }
      TabView(selection: $insp.tab) {
        CardsSwiftUIView()
          .tabItem {
            Image(systemName: "square.fill")
            Text("Cards")
        }.tag(0)
        
        FightSectionSwiftUIView()
          .tabItem {
            Image(systemName: "circle.fill")
            Text("Fight")
        }.tag(1)
        
        SettingsSwiftUIView().environmentObject(playback)
          .tabItem {
            Image(systemName: "arrowtriangle.down.fill")
            Text("SettingsTab")
        }.tag(2)
      }
      .font(.headline)
    }
  }
  
  var playbackRC: some View {
    VStack(spacing: 0) {
      primaryColor(dinFont(Text(playback.selectedReplay ?? ""))).fixedSize().padding()
      Divider()
      Spacer()
      VideoRC({
        withAnimation(self.animation) {
          self.ofc = height
        }
        self.insp.shouldShowVideoSelectView = true
        withDelay({
          self.insp.switchRCType(.Basic)
        }, self.animationTime)
      }).environmentObject(playback)
    }.background(UIGlobals.modalSheetBackground)
      
      .offset(CGSize(width: 0, height: ofc))
      .onAppear(perform: {
        withAnimation(self.animation) {
          self.ofc = 0
        }
        withDelay({
          Vibration.impact()
        }, self.animationTime)
      })
      .onDisappear(perform: {
        self.ofc = -height
      })
  }
  
  var body: some View {
    ZStack {
      general
      if insp.shouldShowVideoRCView {
        playbackRC
      }
    }
  }
}

struct RcSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    RcSwiftUIView()
      .environmentObject(PlaybackControls())
      .environmentObject(InspSettings())
      .environmentObject(FightSettings())
  }
}
