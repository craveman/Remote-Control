//
//  WeaponsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct WeaponsButtonSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var rcModel: InspSettings
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        ZStack {
          Image("weapon").resizable()
        }.frame(width: 48, height: 48)
        primaryColor(dinFont(Text("weapon")))
      }
    }.foregroundColor(primaryColor)
      .frame(width: width / 2, height: mediumHeightOfButton())
      .border(Color.gray, width: 0.5)
      .sheet(isPresented: self.$showModal) {
        WeaponsSwiftUIView()
          .environmentObject(self.settings)
          .environmentObject(self.rcModel)
    }
  }
}

fileprivate let weaponsList: [(Weapon, String)] = [
  (.epee, "Epee"),
  (.sabre, "Sabre"),
  (.foil, "Foil"),
]

fileprivate struct WeaponButton: View {
  var id = -1;
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var rcModel: InspSettings
  func isSelected() -> Bool {
    return weaponsList[self.id].0 == settings.weapon
  }
  
  var body: some View {
    VStack {
      if (id >= 0 && weaponsList.count > id) {
        Button(action: {
          rs.competition.weapon = weaponsList[self.id].0
          self.rcModel.isLockedForRaceState = true
          Vibration.on()
        }) {
          dinFont(
            Text(NSLocalizedString("\(weaponsList[id].1)", comment: "")),
            UIGlobals.popupContentFontSize)
        }
        .padding([.vertical])
        .frame(width: width)
        .background(isSelected() ? UIGlobals.activeButtonBackground_SUI : Color.clear)
        .foregroundColor(isSelected() ? secondaryColor : primaryColor)
      } else {
        Text("Invalid Button id")
      }
    }
    
  }
}

struct WeaponsSwiftUIView: View {
  @Environment(\.presentationMode) var presentationMode
  @EnvironmentObject var settings: FightSettings
  @EnvironmentObject var rcModel: InspSettings
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Weapon")
      Spacer()
      VStack(spacing: 0) {
        ForEach(0..<weaponsList.count) { (i: Int) in
          WeaponButton(id: i)
        }
      }
      Spacer()
      Divider()
      HStack(spacing: 0) {
        ConfirmModalButton(action: {
          self.presentationMode.wrappedValue.dismiss()
        }, text: "done", color: .green)
      }
      .padding([.vertical]).frame(width: width)
    }
    
  }
}

struct WeaponsSwiftUIView_Previews: PreviewProvider {
  static var settings: FightSettings = FightSettings()
  static var rcModel: InspSettings = InspSettings()
  static var previews: some View {
    WeaponsSwiftUIView()
      .environmentObject(settings)
      .environmentObject(rcModel)
  }
}


struct WeaponsButtonSwiftUIView_Previews: PreviewProvider {
  static var settings: FightSettings = FightSettings()
  static var rcModel: InspSettings = InspSettings()
  static var previews: some View {
    WeaponsButtonSwiftUIView()
      .environmentObject(settings)
      .environmentObject(rcModel)
  }
}
