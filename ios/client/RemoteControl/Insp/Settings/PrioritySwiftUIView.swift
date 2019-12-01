//
//  PrioritySwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct PriorityButtonSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        SmallDice().frame(width: 48, height: 48)
        primaryColor(dinFont(Text("priority")))
      }
    }.foregroundColor(primaryColor)
      .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
      .sheet(isPresented: self.$showModal) {
        PrioritySwiftUIView().environmentObject(self.settings)
    }
  }
}

struct PrioritySwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  @State var hasPriority: Bool = rs.persons.left.isPriority || rs.persons.right.isPriority
  
  func setPriorityAction () {
    if Bool.random() {
      rs.persons.left.setPriority()
    } else {
      rs.persons.right.setPriority()
    }
    self.updateState()
  }
  
  func clear() {
    rs.persons.resetPriority()
    self.updateState()
  }
  
  func updateState() {
    self.hasPriority = rs.persons.left.isPriority || rs.persons.right.isPriority
  }
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Priority")
      Spacer()
      Button(action: {
        print("set priority")
        self.setPriorityAction()
      }) {
        Dice().frame(width: 128, height: 128)
      }.padding().foregroundColor(primaryColor)
      
      Spacer()
      VStack(spacing: 0) {
        Divider()
        Button(action: {
          print("reset priority")
          self.clear()
        }) {
          primaryColor(dinFont(Text("do not show")))
        }
          .padding([.vertical])
        .frame(width: width)
      }
      .opacity(self.hasPriority ? 1.0 : 0.0)
       Divider()
      HStack {
        ConfirmModalButton(action: {
          self.settings.fightSwitchActiveTab = 0
          self.settings.tab = 1
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.padding([.vertical]).frame(width: width)
    }
    
  }
}

fileprivate struct Dice: View {
  var dotsPadding: CGFloat = 20
  var scaleFactor: CGFloat = 1
  var body: some View {
    ZStack {
      Image(systemName: "square").resizable()
      Image(systemName: "circle.fill").offset(x: dotsPadding, y: -dotsPadding).scaleEffect(scaleFactor)
      Image(systemName: "circle.fill").scaleEffect(scaleFactor)
      Image(systemName: "circle.fill").offset(x: -dotsPadding, y: dotsPadding).scaleEffect(scaleFactor)
    }
  }
}

fileprivate struct SmallDice: View {
  private var dotsPadding: CGFloat = 20
  private var scaleFactor: CGFloat = 0.33
  var body: some View {
    Dice(dotsPadding: dotsPadding, scaleFactor: scaleFactor)
  }
}


struct PrioritySwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PrioritySwiftUIView()
  }
}


struct PriorityButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    PriorityButtonSwiftUIView()
  }
}
