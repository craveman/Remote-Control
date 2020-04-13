//
//  VideoReplaysSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VideoReplaysButtonSwiftUIView: View {
  
  
  @Binding var showModal: Bool;
  @EnvironmentObject var playback: PlaybackControls
  
  static var modal: some View {
    VideoReplaysSwiftUIView()
    .background(UIGlobals.modalSheetBackground)
    
  }
  
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
      .sheet(isPresented: self.$showModal, onDismiss: {
        if (self.playback.selectedReplay != nil) {
          self.playback.eject()
        }
      }) {
        VideoReplaysButtonSwiftUIView.modal.environmentObject(self.playback)
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
  
  var records: some View {
    ReplaysListUIView().environmentObject(playback)
  }
  
  var recordsToggle: some View {
    VStack(spacing: 0) {
      Divider()
      RecordModeToggleButtonSwiftUIView().environmentObject(playback)
      Divider()
    }
  }
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Video replays")
      if !playback.canPlay {
        Group() {
          ReplaySelectorsUIView(selectedTab: $currentView)
        }
      }
      
      
      Group() {
        if currentView == 0 {
          Group() {
            Spacer()
            picker
          }
        }
        if currentView == 1 {
          records
        }
      }
      Spacer()
      if !playback.canPlay {
        Group() {
          if currentView == 0 {
            Divider()
          }
          if currentView == 1 {
            recordsToggle
            
          }
          HStack {
            ConfirmModalButton(vibrate: false, action: {
              self.presentationMode.wrappedValue.dismiss()
            }, color: .green)
          }.padding([.vertical]).frame(width: width)
        }
      }
    }
  }
}

struct ReplaysListUIView: View {
  @EnvironmentObject var playback: PlaybackControls
  @State var selectedReplay: Int?
  
  private func simulateLoadingItem() -> Void {
    // do staff
    withDelay({
      self.playback.loaded()
    }, 2)
  }
  
  func doSelect(_ index: Int) {
    //    Vibration.on()
    
    
    self.selectedReplay = index
    self.playback.choose(name: self.getTitle(index))
//    self.simulateLoadingItem()
    Vibration.impact()
  }
  
  func getReversedIndex(_ index: Int) -> Int {
    return self.playback.replaysList.count - index - 1
  }
  
  func isSelected(_ index: Int) -> Bool {
    return self.selectedReplay == index
  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.playback.replaysList[index])"
  }
  
  var itemSelectorView: some View {
    Group() {
      if playback.replaysList.count == 0 {
        Spacer()
        primaryColor(dinFont(Text("No records found"))).fixedSize()
      }
      if playback.replaysList.count > 0 {
        ScrollView() {
          VStack(alignment: .leading, spacing: 0) {
            Divider()
            ForEach(0..<playback.replaysList.count, id: \.self) { (i: Int) in
              Group() {
                Button(action: { self.doSelect(self.getReversedIndex(i)) }) {
                  primaryColor(dinFont(Text(self.getTitle(self.getReversedIndex(i))))).fixedSize()
                    .frame(width: width - 16, alignment: .leading)
                }.padding().frame(width: width)
                Divider()
              }
              
            }
          }.frame(width: width, alignment: .leading)
        }
      }
    }
  }
  
  var rcView: some View {
    VStack(spacing: 0) {
      primaryColor(dinFont(Text(getTitle(self.selectedReplay!)))).fixedSize()
      VideoRC({
        self.selectedReplay = nil
        self.playback.eject()
      }).environmentObject(playback)
    }
  }
  
  var selectedView: some View {
    VStack() {
      Group() {
        if !self.playback.canPlay {
          Group() {
            primaryColor(dinFont(Text("Loading"))).fixedSize()
            primaryColor(dinFont(Text("\(getTitle(selectedReplay!))"))).fixedSize()
            //          Divider()
            //          VideoButton().environmentObject(playback)
            //          Divider()
          }
        }
        if self.playback.canPlay {
          rcView
        }
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


fileprivate struct RecordModeToggleButtonSwiftUIView: View {
  @EnvironmentObject var playback: PlaybackControls
  
  func getText () -> String {
    return self.playback.isRecordActive ? "recording is active" : "recording turned off"
  }
  
  
  func getColor () -> Color {
    return self.playback.isRecordActive ? Color(.sRGBLinear, red: 0.9, green: 0.1, blue: 0.1, opacity: 0.12) : Color.clear
  }
  
  var body: some View {
    HStack(spacing: 0) {
      Button(action: {
        self.playback.isRecordActive.toggle()
        Vibration.on()
      }) {
        primaryColor(dinFont(Text(NSLocalizedString(getText(), comment: ""))))
      }
        .padding([.vertical])
      .frame(width: !playback.isRecordActive ? width : width * 0.80)
      .background(getColor())
      if playback.isRecordActive {
        withAnimation{
          Group() {
            
            Button(action: {
              rs.video.cut()
            }){
              ZStack {
                Image(systemName: "scissors").resizable()
              }.frame(width: 32, height: 24).foregroundColor(primaryColor)
            }.frame(width: width * 0.20)
          }.transition(AnyTransition.move(edge: .trailing))
        }
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
  
  func isTabSelected(_ index: Int) -> Bool {
    return self.selectedTab == index
  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.tabs[index])"
  }
  
  var body: some View {
    HStack(spacing: 0) {
      ForEach(0..<tabs.count) { (i: Int) in
        InspTabSelector(title: self.getTitle(i), action: { self.doSelect(i) }, isSelected: self.isTabSelected(i))
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
  @State static var showModal = false
  static var previews: some View {
    VideoReplaysButtonSwiftUIView(showModal: VideoReplaysButtonSwiftUIView_Previews.$showModal).environmentObject(PlaybackControls())
  }
}
