//
//  DisconnectSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct ConnectButton: View {
  @EnvironmentObject var insp: InspSettings
  var body: some View {
    Button(action: {
      rs.connection.disconnect()
      self.insp.quit()
    }) {
      VStack(spacing: 0){
        Image(systemName: "link").resizable().scaledToFit()
          .frame(width: 32, height: 32).foregroundColor(.green)
        primaryColor(dinFont(Text(NSLocalizedString("Connect", comment: "")))).fixedSize()
          
      }.padding().frame(width: width)
    }
  }
}

struct DisconnectButtonSwiftUIView: View {
  var body: some View {
    CommonModalButton(imageName: "multiply", imageColor: primaryColor, buttonType: .disconnect, text: "disconnect",  action: {
      print("DisconnectButtonSwiftUIView:action")
      rs.connection.disconnect()
    }, onDismiss: {
      
      print("DisconnectButtonSwiftUIView:onDismiss")
      
      
    }, border: Color.clear ) {
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
      }
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(vibrate: false, action: {
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
