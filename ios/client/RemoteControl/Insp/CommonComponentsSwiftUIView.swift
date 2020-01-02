//
//  CommonButtonsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct CommonLabel : View {
  var label: String
  var ns: String = ""
  var comment: String = ""
  var fontName: String = "DIN Alternate"
  var fontSize = UIGlobals.appDefaultFontSize
  
  var body: some View {
    Text(NSLocalizedString("\(ns.count > 0 ? ns + "." : "")" + "label \(self.label)", comment: "\(self.comment)"))
      .font(
        Font.custom(self.fontName, size: self.fontSize).bold()
    )
  }
  
}

struct CommonModalHeader: View {
  var title = ""
  var body: some View {
    dinFont(Text(NSLocalizedString("title \(self.title)", comment: ""))).foregroundColor(.white).padding([.vertical]).fixedSize().frame(width: width).background(UIGlobals.headerBackground_SUI)
  }
}

struct CommonButton: View {
  var action: () -> Void
  var text = "Button"
  var imageName = ""
  var imageColor = primaryColor
  var frame = getButtonFrame(.withImage)
  var border = Color.gray
  var body: some View {
    Button(action: {
      self.action()
    }) {
      VStack{
        if imageName.count > 0 {
          Image(systemName: imageName).resizable().scaledToFit()
            .frame(width: 32, height: 32).foregroundColor(self.imageColor)
        }
        primaryColor(dinFont(Text(NSLocalizedString("\(self.text)", comment: "")))).fixedSize()
      }.frame(width: frame.idealWidth)
    }.frame(width: frame.idealWidth, height: frame.idealHeight, alignment: frame.alignment)
      .border(self.border, width: 0.5)
  }
}

struct CommonModalButton<Content>: View where Content: View {
  @EnvironmentObject var settings: FightSettings
  @Binding var showModal: Bool
  
  private let uuid = UUID()
  
  let content: () -> Content
  var text = "Button"
  var imageName = ""
  var imageColor: Color = primaryColor
  var buttonType: ButtonType
  var frame = getButtonFrame(.withImage)
  
  var action: () -> Void
  var onDismiss: () -> Void
  var border: Color
  
  init(imageName: String?, imageColor: Color?, buttonType: ButtonType = .withImage, text: String, action: @escaping () -> Void = {}, onDismiss: @escaping () -> Void = {}, border: Color = Color.gray, showModal: Binding<Bool> = State(initialValue: false).projectedValue, @ViewBuilder content: @escaping () -> Content) {
    self.buttonType = buttonType
    self.frame = getButtonFrame(buttonType)
    self.text = text
    self.action = action
    self.onDismiss = onDismiss
    self.content = content
    if imageName?.count ?? 0 > 0 {
      self.imageName = imageName!
    }
    
    if ((imageColor) != nil) {
      self.imageColor = imageColor!
    }
    self.border = border
    self._showModal = showModal
  }
  
  var body: some View {
    CommonButton(action: {
      self.showModal.toggle()
      self.action()
    }, text: self.text, imageName: self.imageName, imageColor: self.imageColor, frame: self.frame, border: self.border)
      .sheet(isPresented: self.$showModal, onDismiss: self.onDismiss) {
        self.content()
    }
  }
}

struct ConfirmModalButton: View {
  var action: () -> Void = {}
  var text = "done"
  var color = primaryColor
  var imageName = "checkmark"
  var mode: Image.TemplateRenderingMode = .original
  var body: some View {
    Button(action: {
      self.action()
      Vibration.on()
    }) {
      VStack{
        if self.imageName.count > 0 {
          Image(systemName: self.imageName).resizable().scaledToFit()
            .frame(width: 36, height: 36).foregroundColor(self.color)
        }
        primaryColor(dinFont(
          Text(NSLocalizedString("\(self.text)", comment: "")),
          UIGlobals.appSmallerFontSize))
      }.padding(.all, 24)
    }
    
  }
}


struct CommonButtonsSwiftUIView: View {
  var body: some View {
    VStack {
      ConfirmModalButton(action: {
        
      }, text: "cancel",
         color: primaryColor, imageName: "multiply")
      CommonButton(action: { })
      CommonModalButton(imageName: "arrow.clockwise.icloud", imageColor: .blue, text: "plus", content: {
        Text("Common modal")
      })
    }
  }
}

struct CommonFloatSlider: View {
  @Binding var sliderValue: Double
  var minimumValue: Double = 0.0
  var maximumvalue = 100.0
  var formatter: (_ value: Double) -> String = {value in
    return "\(Int(value))"
  }
  var body: some View {
    VStack {
      HStack {
        primaryColor(dinFont(Text("\(formatter(minimumValue))")))
        Slider(value: $sliderValue, in: minimumValue...maximumvalue)
        primaryColor(dinFont(Text("\(formatter(maximumvalue))")))
      }.padding()
      primaryColor(dinFont(Text("\(formatter(sliderValue))")))
    }
  }
}

func getPickerOptions(_ count: Int) -> [[String]] {
  guard count > 0 else {
    return []
  }
  return Array(0...(count-1)).map({_ in Array(0...9).map({"\($0)"})})
}

struct CommonPicker: View {
  @Binding var selected: Int
  var options: [String] = getPickerOptions(1)[0]
  var label = ""
  var body: some View {
    
    GeometryReader { geometry in
      
      HStack {
        Picker(selection: self.$selected, label: Text("\(self.label)")) {
          ForEach(0 ..< self.options.count) {
            Text(self.options[$0]).tag($0)
          }
        }.frame(width: geometry.size.width, height: geometry.size.height)
        .clipped()
      }
    }
  }
  
}


struct CommonBundlePicker: View {
  @Binding var selected: [Int]
  var options: [[String]] = []
  var label = ""
  var body: some View {
    GeometryReader { geometry in
      
      HStack
        {
          ForEach(0 ..< self.options.count) { pos in
            
            Picker(selection: self.$selected[pos], label: Text("\(self.label)")) {
              ForEach(0 ..< self.options[pos].count) {
                Text(self.options[pos][$0]).tag($0)
              }
            }.pickerStyle(WheelPickerStyle())
              .frame(width: geometry.size.width / CGFloat(self.options.count), height: geometry.size.height)
              .clipped()
            
          }
          //             Picker(selection: self.$selection, label: Text(""))
          //             {
          //                  ForEach(0 ..< self.data1.count)
          //                  {
          //                      Text(self.data1[$0])
          //                         .color(Color.white)
          //                         .tag($0)
          //                  }
          //              }
          //              .pickerStyle(.wheel)
          //              .fixedSize(horizontal: true, vertical: true)
          //              .frame(width: geometry.size.width / 2, height: geometry.size.height, alignment: .center)
          //
          //
          //              Picker(selection: self.$selection2, label: Text(""))
          //              {
          //                   ForEach(0 ..< self.data2.count)
          //                   {
          //                       Text(self.data2[$0])
          //                           .color(Color.white)
          //                           .tag($0)
          //                   }
          //              }
          //              .pickerStyle(.wheel)
          //              .fixedSize(horizontal: true, vertical: true)
          //              .frame(width: geometry.size.width / 2, height: geometry.size.height, alignment: .center)
          
      }
    }
    
    
  }
  
}

struct InspTabSelector: View {
  var title: String = "Button"
  var action: () -> Void
  var isSelected: Bool = false
  var size = getButtonFrame(.basic)
  var body: some View {
    Button(action: self.action) {
      dinFont(Text(NSLocalizedString("tab \(title)", comment: ""))
        .foregroundColor(!self.isSelected ? primaryColor : .white)
      ).fixedSize()
    }
    .frame(width: self.size.idealWidth, height: self.size.idealHeight, alignment: self.size.alignment)
    .foregroundColor(!self.isSelected ? primaryColor: .white)
    .background(self.isSelected ? UIGlobals.activeButtonBackground_SUI : nil)
  }
}

struct PickerHolderView: View {
  @State var testSelect = 1
  @State var testBundleSelect = [1, 1]
  var body: some View {
    VStack {
      CommonPicker(selected: $testSelect, options: ["2","3", "4"], label: "Select")
      CommonBundlePicker(
        selected: $testBundleSelect, options: [["2","3", "4"], ["5", "6", "7"]], label: "Bundle Select"
      )
    }
    
  }
}

struct CommonButtonsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    CommonButtonsSwiftUIView()
  }
}


struct CommonButtonsSwiftUIView2_Previews: PreviewProvider {
  static var previews: some View {
    PickerHolderView()
  }
}

// .actionSheet(isPresented: self.$showModal) {
//          ActionSheet(title: "Action")
fileprivate var testActionSheet = ActionSheet(title: Text("Action title"), message: Text("Some message"),
                                              buttons: [
                                                ActionSheet.Button.cancel({print("cancel")}),
                                                ActionSheet.Button.default(Text("Go"), action: {print("go")})
])
