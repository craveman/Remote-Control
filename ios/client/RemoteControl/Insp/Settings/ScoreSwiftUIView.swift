//
//  ScoreSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 24.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI



struct ScoreButtonSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @State var showModal = false
  var body: some View {
    Button(action: {
      self.showModal.toggle()
    }) {
      VStack {
        HStack {
          primaryColor(dinFont(Text("\(self.settings.leftScore)"), 48)).frame(width: 80, alignment: .trailing)
          primaryColor(dinFont(Text(":"), 48))
          primaryColor(dinFont(Text("\(self.settings.rightScore)"), 48)).frame(width: 80, alignment: .leading)
        }
        
        primaryColor(dinFont(Text("score")))
      }
      
      
    }.foregroundColor(primaryColor)
      .frame(width: width, height: mediumHeightOfButton())
      .sheet(isPresented: self.$showModal) {
        ScoreSwiftUIView(left: self.settings.leftScore, right: self.settings.rightScore).environmentObject(self.settings)
    }
  }
}


struct ScoreSwiftUIView: View {
  @EnvironmentObject var settings: FightSettings
  @Environment(\.presentationMode) var presentationMode
  @State var leftDeci = 0
  @State var leftUnit = 0
  @State var rightDeci = 0
  @State var rightUnit = 0
  let pickerWidth = 19 / 20 * width / 6
  
  
  func getScore() -> (left: UInt8, right: UInt8) {
    var selectionLeft = [leftDeci, leftUnit]
    var selectionRight = [rightDeci, rightUnit]
    let mults = [10, 1]
    func calc(_ arr: [Int]) -> UInt8 {
      var res = 0;
      arr.enumerated().forEach({ (index, element) in
        res += mults[index] * element
      })
      return UInt8(res)
    }
    
    return (calc(selectionLeft), calc(selectionRight))
  }
  
  
  init(left: UInt8, right: UInt8) {
    self._leftDeci = State(wrappedValue: Int(left / 10))
    self._leftUnit = State(wrappedValue: Int(left % 10))
    self._rightDeci = State(wrappedValue: Int(right / 10))
    self._rightUnit = State(wrappedValue: Int(right % 10))
  }
  
  var body: some View {
    VStack(spacing: 0) {
      CommonModalHeader(title: "Set score")
      Spacer()
      HStack(spacing: 0){
        Spacer()
        CommonPicker(selected: self.$leftDeci).frame(width: pickerWidth)
        CommonPicker(selected: self.$leftUnit).frame(width: pickerWidth)
        VStack(spacing: 0){
          Spacer()
          Text(" ")
          Spacer()
        }.frame(width: 3*width/20)
        CommonPicker(selected: self.$rightDeci).frame(width: pickerWidth)
        CommonPicker(selected: self.$rightUnit).frame(width: pickerWidth)
        Spacer()
      }
      Spacer()
      Divider()
      HStack {
        ConfirmModalButton(action: {
          let scores = self.getScore()
          
          self.settings.leftScore = scores.left
          self.settings.rightScore = scores.right
          
          self.presentationMode.wrappedValue.dismiss()
        }, color: .green)
      }.padding([.vertical]).frame(width: width)
    }
  }
}

struct ScoreSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    ScoreSwiftUIView(left: 0, right: 19)
  }
}


struct ScoreButtonSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    ScoreButtonSwiftUIView().environmentObject(FightSettings())
  }
}
