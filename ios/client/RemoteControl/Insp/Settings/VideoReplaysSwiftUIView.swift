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
//      self.playback.refreshVideoList()
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
        if self.playback.selectedReplay != nil && !self.playback.canPlay {
          rs.video.replay.stopLoading()
        }
        print("sheet: $insp.shouldShowVideoSelectView -> onDismiss")
      }) {
        self.modal.environmentObject(self.playback).edgesIgnoringSafeArea(.bottom)
    }
  }
}

final class PlayersReplaysCountStore: ObservableObject {
  var playbacksLeftCount = Int(rs.video.replay.leftCounter) {
    didSet {
      rs.video.replay.setCounter(left: UInt8(self.playbacksLeftCount))
      print("playbacksLeftCount", self.playbacksLeftCount)
    }
  }
  var playbacksRightCount =  Int(rs.video.replay.rightCounter) {
    didSet {
      rs.video.replay.setCounter(right: UInt8(self.playbacksRightCount))
      print("playbacksRightCount", self.playbacksRightCount)
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
    
    self.playback.choose(filename: self.playback.replaysList[index])
    Vibration.notification()
  }
  
  func getReversedIndex(_ index: Int) -> Int {
    return self.playback.replaysList.count - index - 1
  }
  
  func getTitle(_ index: Int) -> String {
    return FileNameConverter.getTitle(self.playback.replaysList[index])
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
            primaryColor(dinFont(Text("\(self.playback.selectedReplay?.title ?? "-")"))).fixedSize()
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


public class FileNameConverter {
  private static let scorePattern = "(?<left>\\d+)_(?<right>\\d+)"
  private static let timePattern = "(?<time>\\d+_\\d+_\\d+)"
  
  private static func getKnownFileNameFormatRegex() -> NSRegularExpression? {
    
    let pattern = "^\(scorePattern)[^\\d]{1,3}\(timePattern)"
    do {
      let regex = try NSRegularExpression(pattern: pattern, options: NSRegularExpression.Options.caseInsensitive)
      return regex
    } catch {
      return nil
    }
    
  }
  
  private static func getFileExtRegex() -> NSRegularExpression? {
    do {
      let regex = try NSRegularExpression(pattern: "[.][\\w\\d]{2,5}$", options: NSRegularExpression.Options.caseInsensitive)
      return regex
    } catch {
      return nil
    }
    
  }
  
  static func sortByTimeAsc(_ a: String, _ b: String) -> Bool {
    let regex = getKnownFileNameFormatRegex()
    var aTime: String?
    var bTime: String?
    if regex != nil {
      let aMatches = regex!.firstMatch(in: a, options: [], range: NSRange(location: 0, length: a.count))
      let bMatches = regex!.firstMatch(in: b, options: [], range: NSRange(location: 0, length: b.count))
      
      if aMatches != nil, let aTimeRange = Range(aMatches!.range(withName: "time"), in: a) {
          aTime = "\(a[aTimeRange])"
      }
      
      if bMatches != nil, let bTimeRange = Range(bMatches!.range(withName: "time"), in: b) {
          bTime = "\(b[bTimeRange])"
      }
    }
    
    if aTime != nil, bTime != nil {
      return aTime! < bTime!
    }
    
    return true
  }
  
  static func getTitle(_ fileName: String) -> String {
    
    let rng = NSRange(location: 0, length: fileName.count)
    if let regex = getKnownFileNameFormatRegex() {
      if let matches = regex.firstMatch(in: fileName, options: [], range: rng) {
        var leftScore: Substring?
        var rightScore: Substring?
        var time: String?
        
        if let leftScoreRange = Range(matches.range(withName: "left"), in: fileName) {
          leftScore = fileName[leftScoreRange]
        }
        
        if let rightScoreRange = Range(matches.range(withName: "right"), in: fileName) {
          rightScore = fileName[rightScoreRange]
        }
        
        if let timeRange = Range(matches.range(withName: "time"), in: fileName) {
          time = "\(fileName[timeRange])".replacingOccurrences(of: "_", with: ":")
        }
        if let l = leftScore, let r = rightScore, let t = time {
          return "\(l) - \(r)  \(t)"
        }
      }
    }
    
    let firstSpace = fileName.firstIndex(of: ".") ?? fileName.endIndex
    let name = fileName[..<firstSpace]
    
    return "\(name)"
  }
}
