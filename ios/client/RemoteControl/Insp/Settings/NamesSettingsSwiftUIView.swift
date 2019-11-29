//
//  NamesSettingsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct NamesSettingsButtonSwiftUIView: View {
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        primaryColor(dinFont(Text("John..."), 48)).fixedSize()
        primaryColor(dinFont(Text("names")))
      }
      
      
    }.foregroundColor(primaryColor)
      .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
      .sheet(isPresented: self.$showModal) {
        NamesSettingsSwiftUIView()
    }
  }
}


struct NamesSettingsSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @State var leftName = rs.persons.left.name
  @State var rightName = rs.persons.right.name
  
  private func endEditing() {
    UIApplication.shared.endEditing()
    if (rs.persons.left.name != self.leftName) {
      rs.persons.left.name = self.leftName
    }
    if (rs.persons.right.name != self.rightName) {
      rs.persons.right.name = self.rightName
    }
  }
  var body: some View {
    
    VStack(spacing: 0) {
      CommonModalHeader(title: "Set names")
      
      ScrollView {
        Background {
          VStack(spacing: 0) {
            VStack(spacing: 0) {
              dinFont(Text("Left player")).alignmentGuide(.leading, computeValue: {_ in 0})
              TextField("", text: self.$leftName) {
                self.endEditing()
                
              }
                .background(Color.init(.sRGB, red: 0, green: 0, blue: 0, opacity: 0.05))
                .padding()
                
            }
            
            VStack(spacing: 0) {
              dinFont(Text("Right player")).alignmentGuide(.leading, computeValue: {_ in 0})
              TextField("", text: self.$rightName) {
                self.endEditing()
              }
                .background(Color.init(.sRGB, red: 0, green: 0, blue: 0, opacity: 0.05))
                .padding()
                
            }
            Spacer()
          }.padding(.top)
        
          }.onTapGesture {
              self.endEditing()
          }
      }
      
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
    }
  }
}

struct NamesSettingsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    NamesSettingsSwiftUIView()
  }
}



struct NamesSettingsButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    NamesSettingsButtonSwiftUIView()
  }
}
