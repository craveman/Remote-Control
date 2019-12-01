//
//  DisconnectSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct DisconnectButtonSwiftUIView: View {
  var body: some View {
    CommonModalButton(imageName: "multiply", imageColor: primaryColor, buttonType: .disconnect, text: "disconnect",  action: {
      print("DisconnectButtonSwiftUIView:action")
    }, onDismiss: {
      
      print("DisconnectButtonSwiftUIView:onDismiss")
      
      
    } ) {
      DisconnectSwiftUIView()
    }
  }
}

struct DisconnectSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  var body: some View {
    VStack{
      CommonModalHeader(title: "Disconnect")
      Spacer()
      VStack {
        ZStack{
          Image(systemName: "multiply").resizable()
        }
        .frame(width: 128, height: 128)
        .foregroundColor(primaryColor)
        
        primaryColor(dinFont(Text("Disconnect this Remote Control?"))).padding(.top).fixedSize()
      }.onTapGesture(count: 1, perform: {
        rs.connection.disconnect()
      })
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "cancel", color: primaryColor, imageName: "chevron.left")
        ConfirmModalButton(action: {
          rs.connection.disconnect()
          self.presentationMode.wrappedValue.dismiss()
        }, text: "disconnect", color: .red, imageName: "power")
      }.padding([.vertical]).frame(width: width)
    }
  }
}

struct DisconnectSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    DisconnectSwiftUIView()
  }
}


struct DisconnectButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    DisconnectButtonSwiftUIView()
  }
}
