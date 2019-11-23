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
        ZStack {
          Image(systemName: "square").resizable()
          Image(systemName: "circle.fill").offset(x: 15, y: -15).scaleEffect(0.5)
          Image(systemName: "circle.fill").scaleEffect(0.5)
          Image(systemName: "circle.fill").offset(x: -15, y: 15).scaleEffect(0.5)
        }.frame(width: 48, height: 48)
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
  @Environment(\.presentationMode) var presentationMode
    var body: some View {
      VStack(spacing: 0) {
        Spacer()
        Button(action: {
          print("set priority")
          self.setPriorityAction()
        }) {
          ZStack {
            Image(systemName: "square").resizable()
            Image(systemName: "circle.fill").offset(x: 20, y: -20)
            Image(systemName: "circle.fill")
            Image(systemName: "circle.fill").offset(x: -20, y: 20)
          }
          
        }.frame(width: 128, height: 128).foregroundColor(primaryColor)
        
        Spacer()
        Button(action: {
          print("reset priority")
          self.clear()
        }) {
          primaryColor(dinFont(Text("do not show")))
        }
        .frame(width: width).padding(.top).padding(.bottom)
        .border(Color.gray, width: 0.5)
        .opacity(self.hasPriority ? 1.0 : 0.0)
        
        HStack {
          ConfirmModalButton(action: {
            self.presentationMode.wrappedValue.dismiss()
          }, color: .green)
        }.frame(width: width).padding(.top).padding(.bottom)
          .border(Color.gray, width: 0.5)
      }
        
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
