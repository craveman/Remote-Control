//
//  VideoReplaysSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

fileprivate let recordModeFeatureEnabled = false

struct VideoReplaysButtonSwiftUIView: View {
  @EnvironmentObject var insp: InspSettings
  @EnvironmentObject var playback: PlaybackControls
  
  var modal: some View {
    VideoReplaysSwiftUIView(currentView: $insp.videoModalSelectedTab)
    .background(UIGlobals.modalSheetBackground)
  }
  
  var body: some View {
    Button(action: {
      rs.video.replay.refresh()
      self.insp.shouldShowVideoSelectView = true
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
      .sheet(isPresented: $insp.shouldShowVideoSelectView, onDismiss: {
        print("sheet(isPresented: self.$showModal, onDismiss")
      }) {
        self.modal.environmentObject(self.playback)
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
  @Binding var currentView: Int
  
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
          if currentView == 0 || !recordModeFeatureEnabled {
            Divider()
          }
          if recordModeFeatureEnabled && currentView == 1 {
            recordsToggle
          }
          HStack {
            ConfirmModalButton(vibrate: false, action: {
              self.playback.eject()
              self.presentationMode.wrappedValue.dismiss()
            }, color: .green)
          }.padding([.vertical]).frame(width: width)
        }
      }
    }
  }
}

struct ReplaysListUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var playback: PlaybackControls
//  @State var selectedReplay: Int?
  
  func doSelect(_ index: Int) {
    //    Vibration.on()
    
    self.playback.choose(name: self.getTitle(index))
    Vibration.notification()
  }
  
  func getReversedIndex(_ index: Int) -> Int {
    return self.playback.replaysList.count - index - 1
  }
//
//  func isSelected(_ index: Int) -> Bool {
//    return self.selectedReplay == index
//  }
  
  func getTitle(_ index: Int) -> String {
    return "\(self.playback.replaysList[index])"
  }
  
  var itemSelectorView: some View {
    Group() {
      if !playback.isEnabled {
        ZStack() {
          primaryColor(dinFont(Text("no camera"), UIGlobals.appSmallerFontSize))
            .frame(width: width)
            .background(Color.red)
        }
      }
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
                Group() {
                  primaryColor(dinFont(Text(self.getTitle(self.getReversedIndex(i)))))
                    .frame(width: width - 16, alignment: .leading)
                    
                }.padding().frame(width: width).fixedSize()
                Divider()
              }.background(UIGlobals.activeBackground_SUI)
              .accessibilityElement()
                .highPriorityGesture(
                  TapGesture().onEnded({ _ in
                    print("tap")
                    self.doSelect(self.getReversedIndex(i))
                  })
              )
              
            }
          }.frame(width: width, alignment: .leading)
        }
      }
    }
  }
  
  var selectedView: some View {
    VStack() {
      Group() {
        if !self.playback.canPlay {
          Group() {
            primaryColor(dinFont(Text("loading video"))).fixedSize().padding()
            primaryColor(dinFont(Text("\(self.playback.selectedReplay!)"))).fixedSize()
          }
        }
        if self.playback.canPlay {
          Spacer().onAppear(perform: {
            print("self.playback.canPlay Spacer onAppear")
          })
        }
      }
    }
  }
  
  var body: some View {
    Group() {
      if self.playback.selectedReplay != nil {
        selectedView
      }
      if self.playback.selectedReplay == nil {
        itemSelectorView
      }
      
    }
  }
}


struct RecordModeToggleButtonSwiftUIView: View {
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
              Vibration.on()
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

struct VideoReplaysSwiftUIView_counters_Previews: PreviewProvider {
  static var previews: some View {
    VideoReplaysSwiftUIView(currentView: .constant(0)).environmentObject(PlaybackControls())
  }
}


struct VideoReplaysSwiftUIView_list_Previews: PreviewProvider {
  static var pbCtrl = PlaybackControls();
  
  static var previews: some View {
    VideoReplaysSwiftUIView(currentView: .constant(1)).environmentObject(pbCtrl)
      .onAppear(perform: {
        pbCtrl.replaysList.append("\(pbCtrl.replaysList.count + 1)")
      })
  }
}

struct VideoReplaysButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    VideoReplaysButtonSwiftUIView()
      .environmentObject(InspSettings())
      .environmentObject(PlaybackControls())
  }
}
