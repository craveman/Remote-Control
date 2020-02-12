//
//  SegmentedPickerElementView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 22.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct SegmentedPickerElementView<Content>: View where Content : View {
    @Binding var selectedElement: Int
    let content: () -> Content

    @inlinable init(_ selectedElement: Binding<Int>, @ViewBuilder content: @escaping () -> Content) {
        self._selectedElement = selectedElement
        self.content = content
    }

    var body: some View {
        GeometryReader { proxy in
            self.content()
                .fixedSize(horizontal: true, vertical: true)
                .frame(minWidth: proxy.size.width, minHeight: proxy.size.height)
                .contentShape(Rectangle())
        }
    }

}

struct SegmentedPickerView: View {
    @Environment (\.colorScheme) var colorScheme: ColorScheme

    var elements: [(id: Int, view: AnyView)]

    @Binding var selectedElement: Int
    @State var internalSelectedElement: Int = 0

    private var width: CGFloat = 420
    private var height: CGFloat = 200
    private var cornerRadius: CGFloat = 20
    private var factor: CGFloat = 0.5

    private var color = Color(UIColor.systemGray)
    private var selectedColor = Color(UIColor.systemGray2)


    init(_ selectedElement: Binding<Int>) {
        self._selectedElement = selectedElement
        self.elements = [
            (id: 0, view: AnyView(SegmentedPickerElementView(selectedElement) {
                Text("4").font(.system(.title))
            })),
            (id: 1, view: AnyView(SegmentedPickerElementView(selectedElement) {
                Text("5").font(.system(.title))

            })),
            (id: 2, view: AnyView(SegmentedPickerElementView(selectedElement) {
                Text("9").font(.system(.title))

            }))
        ]
        self.internalSelectedElement = selectedElement.wrappedValue
    }

    func calcXPosition() -> CGFloat {
        var pos = CGFloat(-self.width) / 2 + CGFloat(self.width/6)
        pos += CGFloat(self.internalSelectedElement) * self.width / CGFloat(self.elements.count)
        return pos
    }

    var body: some View {
        ZStack {
            Rectangle()
                .foregroundColor(self.selectedColor)
                .cornerRadius(self.cornerRadius * self.factor)
                .frame(width: self.width / CGFloat(self.elements.count), height: self.height)
                .offset(x: calcXPosition())
                .animation(.easeInOut(duration: 0.2))

            HStack(alignment: .center, spacing: 0) {
                ForEach(self.elements, id: \.id) { item in
                    item.view
                        .gesture(TapGesture().onEnded { _ in
                            print(item.id)
                            self.selectedElement = item.id
                            withAnimation {
                                self.internalSelectedElement = item.id
                            }
                        })
                }
            }
        }
        .frame(width: self.width, height: self.height)
        .background(self.color)
        .cornerRadius(self.cornerRadius)
        .padding()
    }
}

struct SegmentedPickerView_Previews: PreviewProvider {
    static var previews: some View {
        SegmentedPickerView(.constant(1))
    }
}
