//
//  PrioritySwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct PriorityButtonSwiftUIView: View {
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
        PrioritySwiftUIView()
    }
  }
}

struct PrioritySwiftUIView: View {
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
      Button(action: {
        print("reset priority")
        self.clear()
      }) {
        primaryColor(dinFont(Text("do not show")))
      }
      .frame(width: width).padding([.vertical])
      .border(Color.gray, width: 0.5)
      .opacity(self.hasPriority ? 1.0 : 0.0)
      
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
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
