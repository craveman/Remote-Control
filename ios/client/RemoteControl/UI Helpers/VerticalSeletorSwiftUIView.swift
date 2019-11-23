//
//  VerticalSeletorSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct VerticalSeletorSwiftUIView: View {
    
    
    enum DragState {
        case inactive
        case pressing
        case dragging(translation: CGSize)
        
        var translation: CGSize {
            switch self {
            case .inactive, .pressing:
                return .zero
            case .dragging(let translation):
                return translation
            }
        }
        
        var isActive: Bool {
            switch self {
            case .inactive:
                return false
            case .pressing, .dragging:
                return true
            }
        }
        
        var isDragging: Bool {
            switch self {
            case .inactive, .pressing:
                return false
            case .dragging:
                return true
            }
        }
    }
    
    @GestureState var dragState = DragState.inactive
    @State var viewState = CGSize.zero
    @State var listOffset = CGFloat(0)
    @State var dinamicViewState = CGSize.zero
    var array = 0...10
    var symbolPadding = 6
    var symbolHeight = 60
    
    func fontSize(_ i: Int) -> CGFloat {
        let decim = pow(0.8, abs((5 - i) / 2))
        let result = NSDecimalNumber(decimal: decim)
        return CGFloat(56 * Float(truncating: result))
    }
    
    func getOptionHeight() -> Int {
        return self.symbolHeight + self.symbolPadding * 2
    }
    
    func getColumnHeight() -> CGFloat {
        return CGFloat(array.count * getOptionHeight())
    }
    
    func getNextY() -> CGFloat {
        let y = viewState.height + dragState.translation.height;
        selectedElement = Int(floor((Float(-getColumnHeight() + y) + 434) / Float(getOptionHeight())))
        return y
    }
    
    @State private var selectedElement = 0;
    var body: some View {
        let minimumLongPressDuration = 0.005
        let longPressDrag = LongPressGesture(minimumDuration: minimumLongPressDuration)
            .sequenced(before: DragGesture())
            .updating($dragState) { value, state, transaction in
                switch value {
                // Long press begins.
                case .first(true):
                    state = .pressing
                // Long press confirmed, dragging may begin.
                case .second(true, let drag):
                    state = .dragging(translation: drag?.translation ?? .zero)
                // Dragging ended or the long press cancelled.
                default:
                    state = .inactive
                }
        }
        .onChanged({value in
            guard case .second(true, let drag?) = value else { return }
            self.dinamicViewState.height = drag.translation.height
            
        })
            .onEnded { value in
                guard case .second(true, let drag?) = value else { return }
                self.viewState.width += drag.translation.width
                self.viewState.height += drag.translation.height
        }
        return VStack {
            ForEach((self.array), id: \.self) {
                    dinFont(Text("\($0) \(self.selectedElement)"), self.fontSize($0)).frame(height: CGFloat(self.symbolHeight)).foregroundColor(primaryColor).fixedSize().padding(CGFloat(self.symbolPadding))
                }
            }
            .frame(width: CGFloat(self.symbolHeight), height: getColumnHeight() / 2)
            .offset(y: getNextY())
                .animation(nil)
                .gesture(longPressDrag)
    }
}

struct VerticalSeletorSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView(showsIndicators: false) {
            ZStack{
                Rectangle()
                    .foregroundColor(.green)
                    .frame(width: width, height: 72)
                    .offset(x: 0, y:  38)
                    .animation(.easeInOut(duration: 0.2))
                    .opacity(0.25)
                HStack {
                    //                VerticalSeletorSwiftUIView()
                    //                VerticalSeletorSwiftUIView()
                    //                VerticalSeletorSwiftUIView()
                    VerticalSeletorSwiftUIView().frame(maxHeight: 720).border(Color.gray, width: 0.5)
                }
                
            }.frame(maxHeight: 240).border(Color.gray, width: 0.5)
        }.frame(height: 200)
        
        
    }
}
