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
  @Environment(\.presentationMode) var presentationMode
  @State var currentView = 0 {
    didSet {
      print("currentView", currentView)
    }
  }
  let maxLeftCount = 2
  
  func getPlaybackList() -> [String] {
    var opts: [String] = [];
    for (_, element) in (0..<maxLeftCount+1).enumerated() {
      opts.append("\(element)")
    }
    return opts
  }
  private let pickerWidth: CGFloat = 88
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Video replays")
      ReplaySelectorsUIView(selectedTab: $currentView)
      HStack(spacing: 0){
        CommonPicker(selected: $store.playbacksLeftCount, options: getPlaybackList()).frame(width: pickerWidth).foregroundColor(primaryColor).opacity(currentView == 0 ? 1 : 0.1)
        .disabled(currentView != 0)
        CommonPicker(selected: $store.playbacksRightCount, options: getPlaybackList()).frame(width: pickerWidth).foregroundColor(primaryColor).opacity(currentView == 1 ? 1 : 0.1)
        .disabled(currentView != 1)
      }
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
