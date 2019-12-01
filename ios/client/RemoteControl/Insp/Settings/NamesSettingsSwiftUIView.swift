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
            VStack(alignment: .leading, spacing: 0) {
              dinFont(Text("Left player"),  UIGlobals.appDefaultFontSize)
              TextField(" ", text: self.$leftName) {
                self.endEditing()
                
              }.font(.largeTitle)
                .background(primaryColor.opacity(0.05))
                .accessibility(label: Text("Left player"))
            }.padding()
            Divider()
            VStack(alignment: .leading, spacing: 0) {
              dinFont(Text("Right player"), UIGlobals.appDefaultFontSize)
              TextField(" ", text: self.$rightName) {
                self.endEditing()
              }.font(.largeTitle)
                .background(primaryColor.opacity(0.05))
                .accessibility(label: Text("Right player"))
              }.padding()
            Spacer()
          }
          
        }.onTapGesture {
          self.endEditing()
        }
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
