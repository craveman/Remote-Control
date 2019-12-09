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
  @EnvironmentObject var playback: PlaybackControls
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
        VideoReplaysSwiftUIView().environmentObject(self.playback)
    }
  }
}

struct VideoReplaysSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var playback: PlaybackControls
  @State var currentView = 0 {
    didSet {
      playback.selectedPlayer = currentView == 0 ? .left : .right
    }
  }
  @State var playbackSelected = 0
  
  func getPlaybackList() -> [String] {
    let count = currentView == 0 ? rs.video.replay.leftCounter : rs.video.replay.rightCounter
    var opts: [String] = [];
    for (index, _) in (0..<count).enumerated() {
      opts.append("\(index+1)")
    }
    return opts
  }
  private let pickerWidth: CGFloat = 88
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Video replays")
      ReplaySelectorsUIView(selectedTab: $currentView)
      Spacer()
      if getPlaybackList().count > 0 {
        HStack(spacing: 0){
          Spacer()
          CommonPicker(selected: $playbackSelected, options: getPlaybackList()).frame(width: pickerWidth)
          Spacer()
          
        }
        Divider()
        VideoButton().environmentObject(self.playback)
      }
      if getPlaybackList().count == 0 {
        primaryColor(dinFont(Text("No saved replays found")))
      }
      
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


struct ReplaySelectorsUIView: View {
  private(set) var tabs = ["Left", "Right"]
  @Binding var selectedTab: Int
  
  func doSelect(_ index: Int) {
    print(index)
    //    Vibration.on()
    self.selectedTab = index
  }
  
  func isSelected(_ index: Int) -> Bool {
    return self.selectedTab == index
  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.tabs[index])"
  }
  
  var body: some View {
    HStack(spacing: 0) {
      ForEach(0..<tabs.count) { (i: Int) in
        InspTabSelector(title: self.getTitle(i), action: { self.doSelect(i) }, isSelected: self.isSelected(i))
      }
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
