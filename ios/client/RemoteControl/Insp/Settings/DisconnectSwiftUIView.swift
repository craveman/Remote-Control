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
      HStack {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "cancel", color: primaryColor, imageName: "multiply")
        ConfirmModalButton(action: {
          rs.connection.disconnect()
          self.presentationMode.wrappedValue.dismiss()
        }, text: "disconnect", color: .red)
      }.frame(width: width).padding([.vertical])
        .border(Color.gray, width: 0.5)
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
