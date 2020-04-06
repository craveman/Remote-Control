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
        VideoReplaysSwiftUIView()
          .background(UIGlobals.modalSheetBackground)
          .environmentObject(self.playback)
    }
  }
}

final class PlayersReplaysCountStore: ObservableObject {
  var playbacksLeftCount = Int(rs.video.replay.leftCounter) {
    didSet {
      rs.video.replay.leftCounter = UInt8(self.playbacksLeftCount)
      print(self.playbacksLeftCount)
    }
  }
  var playbacksRightCount =  Int(rs.video.replay.rightCounter) {
    didSet {
      rs.video.replay.rightCounter = UInt8(self.playbacksRightCount)
      print(self.playbacksRightCount)
    }
  }
  
  // @Published var items = ["Jane Doe", "John Doe", "Bob"]
}

struct VideoReplaysSwiftUIView: View {
  @ObservedObject var store = PlayersReplaysCountStore()
  @EnvironmentObject var playback: PlaybackControls
  @Environment(\.presentationMode) var presentationMode
  @State var currentView = 0 {
    didSet {
      print("currentView", currentView)
    }
  }
  let maxCount = Int(RemoteService.VideoManagement.VideoReplayManagement.MAX_COUNTER)
  
  func getPlaybackList() -> [String] {
    var opts: [String] = [];
    for (_, element) in (0..<maxCount+1).enumerated() {
      opts.append("\(element)")
    }
    return opts
  }
  private let pickerWidth: CGFloat = 88
  
  var picker: some View {
    HStack(spacing: 0){
      Spacer()
      CommonPicker(selected: $store.playbacksLeftCount, options: getPlaybackList()).frame(width: pickerWidth)
      Spacer()
      CommonPicker(selected: $store.playbacksRightCount, options: getPlaybackList()).frame(width: pickerWidth)
      Spacer()
    }
  }
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Video replays")
      ReplaySelectorsUIView(selectedTab: $currentView)
      if currentView == 0 {
        picker
      }
      if currentView == 1 {
        Spacer()
        ReplaysListUIView().environmentObject(playback)
        Spacer()
      }
      
      Divider()
      HStack {
        ConfirmModalButton(vibrate: false, action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.padding([.vertical]).frame(width: width)
      
    }
  }
}

struct ReplaysListUIView: View {
  let replays: [String] = []
  @EnvironmentObject var playback: PlaybackControls
  @State var selectedReplay: Int?
  @State var selectedReplayLoadingComlete = false
  
  func startLoadingItem() -> Void {
    // do staff
    withDelay({
      self.selectedReplayLoadingComlete = true
    }, 2)
  }
  
  func doSelect(_ index: Int) {
    //    Vibration.on()
    
    self.selectedReplayLoadingComlete = false
    self.selectedReplay = index
    self.startLoadingItem()
    Vibration.impact()
  }
  
  func isSelected(_ index: Int) -> Bool {
    return self.selectedReplay == index
  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.replays[index])"
  }
  var itemSelectorView: some View {
    Group() {
      if replays.count == 0 {
        primaryColor(dinFont(Text("No records found"))).fixedSize()
      }
      if replays.count > 0 {
        ScrollView() {
          VStack(alignment: .leading, spacing: 0) {
            ForEach(0..<replays.count) { (i: Int) in
              Button(action: { self.doSelect(i) }) {
                Text(self.getTitle(i))
              }.padding()
            }
          }.frame(width: width, alignment: .leading)
        }
      }
    }
  }
  
  var selectedView: some View {
    VStack() {
      if !selectedReplayLoadingComlete {
        Group() {
          primaryColor(dinFont(Text("Loading"))).fixedSize()
          primaryColor(dinFont(Text("\(getTitle(selectedReplay!))"))).fixedSize()
//          Divider()
//          VideoButton().environmentObject(playback)
//          Divider()
        }
      }
      if selectedReplayLoadingComlete {
        VideoRC({
          self.selectedReplay = nil
        }).environmentObject(playback)
      }
      
    }
  }
  
  var body: some View {
    Group() {
      if selectedReplay != nil {
        selectedView
      }
      if selectedReplay == nil {
        itemSelectorView
      }
    }
    
    
  }
}

struct ReplaySelectorsUIView: View {
  private(set) var tabs = ["Counters", "Records"]
  @Binding var selectedTab: Int
  
  func doSelect(_ index: Int) {
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
    VideoReplaysSwiftUIView().environmentObject(PlaybackControls())
  }
}


struct VideoReplaysButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    VideoReplaysButtonSwiftUIView().environmentObject(PlaybackControls())
  }
}
