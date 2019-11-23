//
//  PointsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 12.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

struct PointsSwiftUIView: View {
  
  func startAction() -> Void {
    rs.timer.start()
  }
  func stopAction() -> Void {
    rs.timer.stop()
  }
  var body: some View {
    VStack(spacing: 0) {
      FightControls().border(Color.gray, width: 0.5)
      MyButtonModalView(action: startAction, onDismiss: stopAction)
        .padding(50)
    }.frame(minWidth: width, idealWidth: width, maxWidth: width, minHeight: getSubScreenHeight(), idealHeight: height, maxHeight: .infinity, alignment: .top)
    
  }
}

fileprivate struct FightControls: View {
  var pType: PersonType = .none
  var body: some View {
    HStack(spacing: 0) {
      VStack {
        HoldPassiveButton()
        PointsStepper(pType: .left)
        
      }
      VStack {
        VideoButton()
        PointsStepper(pType: .right)
      }
      
    }
    .border(Color.gray)
  }
}

fileprivate struct MyModalView: View {
  @Environment(\.presentationMode) var presentationMode
  var countdown: UInt32
  
  var body: some View {
    
    VStack {
      dinFont(Text("\(getTimeString(countdown))"), UIGlobals.timerFontSize)
        .padding(CGFloat(20))
        .onTapGesture(count: 1, perform: {
          self.presentationMode.wrappedValue.dismiss()
        })
    }
  }
}

fileprivate struct MyButtonModalView: View {
  var size = getButtonFrame(.fullWidth)
  @State var showModal = false
  @EnvironmentObject var settings: FightSettings
  var action: () -> Void
  var onDismiss: () -> Void
  var body: some View {
    Button(action: {
      print("Button Pushed")
      self.action()
      self.showModal = true
    }) {
      primaryColor(dinFont(Text("\(getTimeString(self.settings.time))"), UIGlobals.timerFontSize))
    }
    .frame(width: size.idealWidth, height: size.idealHeight, alignment: size.alignment)
    .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
      MyModalView(countdown: self.settings.time)
    }
  }
}

struct PointsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PointsSwiftUIView()
  }
}
