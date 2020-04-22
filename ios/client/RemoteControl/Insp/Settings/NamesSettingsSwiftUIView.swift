//
//  NamesSettingsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

let NAME_LENGTH_LIMIT = 30

struct NamesSettingsButtonSwiftUIView: View {
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        primaryColor(dinFont(Text("John..."), 36)).fixedSize().padding(.top, 14)
        primaryColor(dinFont(Text("names")))
      }
      
      
    }.foregroundColor(primaryColor)
      .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
      .sheet(isPresented: self.$showModal) {
        NamesSettingsSwiftUIView()
          .background(UIGlobals.modalSheetBackground)
    }
  }
}

class NameBindingManager: ObservableObject {
  init(_ initialValue: String, updater setter: @escaping (String) -> Void) {
    self.setter = setter
    self.text = initialValue
  }
  var setter: (String) -> Void
  @Published var text = "" {
    didSet {
      if text.count > characterLimit && oldValue.count <= characterLimit {
        text = oldValue
        Vibration.warning()
      }
      setter(text)
      if(text != oldValue) {
        Vibration.notification()
      }
      
    }
  }
  let characterLimit = NAME_LENGTH_LIMIT
}

struct NamesSettingsSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @ObservedObject var leftName = NameBindingManager(rs.persons.left.name, updater: { name in rs.persons.left.name = name})
  @ObservedObject var rightName = NameBindingManager(rs.persons.right.name, updater: { name in rs.persons.right.name = name})
  
  private func endEditing() {
    UIApplication.shared.endEditing()
  }
  
  var body: some View {
    
    VStack(spacing: 0) {
      CommonModalHeader(title: "Set names")
      
      ScrollView {
        Background {
          VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
              HStack(spacing: 0) {
                dinFont(Text("Left player"),  UIGlobals.appDefaultFontSize)
                Spacer()
                if self.leftName.text.count * 2 > NAME_LENGTH_LIMIT {
                  dinFont(Text("\(self.leftName.text.count) / \(NAME_LENGTH_LIMIT)"),  UIGlobals.appDefaultFontSize)
                }
              }
              
              TextField(" ", text: self.$leftName.text) {
                self.endEditing()
                
              }.font(.largeTitle)
                .background(primaryColor.opacity(0.05))
                .accessibility(label: Text("Left player"))
            }.padding()
            Divider()
            VStack(alignment: .leading, spacing: 0) {
              HStack(spacing: 0) {
                dinFont(Text("Right player"), UIGlobals.appDefaultFontSize)
                Spacer()
                if self.rightName.text.count * 2 > NAME_LENGTH_LIMIT {
                  dinFont(Text("\(self.rightName.text.count) / \(NAME_LENGTH_LIMIT)"),  UIGlobals.appDefaultFontSize)
                }
              }
              TextField(" ", text: self.$rightName.text) {
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
