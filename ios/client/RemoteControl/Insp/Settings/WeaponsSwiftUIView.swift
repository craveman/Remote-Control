//
//  WeaponsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct WeaponsButtonSwiftUIView: View {
    @State var showModal = false
    var body: some View {
      Button(action: {
        self.showModal.toggle()
      }) {
        VStack {
          ZStack {
            Image(systemName: "hammer.fill").resizable().rotationEffect(Angle.degrees(180.0))
          }.frame(width: 48, height: 48)
          primaryColor(dinFont(Text("weapon")))
        }
        
        
      }.foregroundColor(primaryColor)
        .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
        .sheet(isPresented: self.$showModal) {
          WeaponsSwiftUIView()
      }
    }
}

struct WeaponsSwiftUIView: View {
    var body: some View {
        Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
    }
}

struct WeaponsSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        WeaponsSwiftUIView()
    }
}


struct WeaponsButtonSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        WeaponsButtonSwiftUIView()
    }
}
