//
//  CommonButtonsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI



struct TimerButton: View {
    var action: () -> Void
    var text = "Button"
    var imageName = ""
    var imageColor = primaryColor
    var frame = getButtonFrame(.withImage)
    
    func getScale() {
        
    }
    
    var body: some View {
        Button(action: {
            self.action()
        }) {
            VStack{
                if imageName.count > 0 {
                    Image(systemName: imageName).resizable().scaledToFit()
                        .frame(width: 32, height: 32).foregroundColor(self.imageColor)
                }
                primaryColor(dinFont(Text(self.text))).fixedSize()
            }
        }.frame(width: frame.idealWidth, height: frame.idealHeight, alignment: frame.alignment)
            .border(Color.gray, width: 0.5)
    }
}

struct TimerModalButton<Content>: View where Content: View {
    @State var showModal = false
    let content: () -> Content
    var text = "Button"
    var imageName = ""
    var imageColor: Color = primaryColor
    var buttonType: ButtonType
    var frame = getButtonFrame(.withImage)
    var action: () -> Void
    var onDismiss: () -> Void
    
    
    init(imageName: String?, imageColor: Color?, buttonType: ButtonType = .withImage, text: String, action: @escaping () -> Void = {}, onDismiss: @escaping () -> Void = {}, @ViewBuilder content: @escaping () -> Content) {
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
        
    }
    
    var body: some View {
        TimerButton(action: {
            self.showModal.toggle()
            self.action()
            
        }, text: self.text, imageName: self.imageName, imageColor: self.imageColor, frame: self.frame)
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
        }) {
            VStack{
                if self.imageName.count > 0 {
                    Image(systemName: self.imageName).resizable().scaledToFit()
                        .frame(width: 36, height: 36).foregroundColor(self.color)
                }
                primaryColor(dinFont(Text(self.text), UIGlobals.appSmallerFontSize))
            }.padding(.all, 24)
        }
        
    }
}


struct CommonButtonsSwiftUIView: View {
    var body: some View {
        VStack {
            Text("Hello, World!")
            ConfirmModalButton(action: {
                
            }, text: "cancel",
               color: primaryColor, imageName: "multiply")
            TimerButton(action: { })
            TimerModalButton(imageName: "plus", imageColor: .blue, text: "arrow.clockwise.icloud", content: {
                Text("Hello, Modal!")
            })
        }
    }
}

struct CommonButtonsSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        CommonButtonsSwiftUIView()
    }
}
