//
//  CardsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI
import Sm02Client

struct HexagonParameters {
    struct Segment {
        let useWidth: (CGFloat, CGFloat, CGFloat)
        let xFactors: (CGFloat, CGFloat, CGFloat)
        let useHeight: (CGFloat, CGFloat, CGFloat)
        let yFactors: (CGFloat, CGFloat, CGFloat)
    }
    
    static let adjustment: CGFloat = 0.085
    
    static let points = [
        Segment(
            useWidth:  (1.00, 1.00, 1.00),
            xFactors:  (0.90, 0.90, 0.90),
            useHeight: (1.00, 1.00, 0.00),
            yFactors:  (0.05, 0.05, 0.00)
        ),
        Segment(
            useWidth:  (1.00, 1.00, 0.10),
            xFactors:  (0.50, 0.50, 0.10),
            useHeight: (1.00, 1.00, 1.00),
            yFactors:  (0.95, 0.95, 1.00)
        ),
        Segment(
            useWidth:  (1.00, 1.00, 1.00),
            xFactors:  (0.10, 0.10, 0.10),
            useHeight: (1.00, 1.00, 1.00),
            yFactors:  (0.9, 0.9, 1.00)
        )
    ]
}

fileprivate struct CardPath: View {
    var body: some View {
        GeometryReader { geometry in
            Path { path in
                var width: CGFloat = min(geometry.size.width, geometry.size.height)
                let height = width
                let xScale: CGFloat = 0.832
                let xOffset = (width * (1.0 - xScale)) / 2.0
                width *= xScale
                path.move(
                    to: CGPoint(
                        x: xOffset + width * 0.1,
                        y: height * (0.10 + HexagonParameters.adjustment)
                    )
                )
                
                HexagonParameters.points.forEach {
                    path.addLine(
                        to: .init(
                            x: xOffset + width * $0.useWidth.0 * $0.xFactors.0,
                            y: height * $0.useHeight.0 * $0.yFactors.0
                        )
                    )
                    
//                    path.addQuadCurve(
//                        to: .init(
//                            x: xOffset + width * $0.useWidth.1 * $0.xFactors.1,
//                            y: height * $0.useHeight.1 * $0.yFactors.1
//                        ),
//                        control: .init(
//                            x: xOffset + width * $0.useWidth.2 * $0.xFactors.2,
//                            y: height * $0.useHeight.2 * $0.yFactors.2
//                        )
//                    )
                }
            }
            .fill(LinearGradient(
                gradient: .init(colors: [Self.gradientStart, Self.gradientEnd]),
                startPoint: .init(x: 0.5, y: 0),
                endPoint: .init(x: 0.5, y: 0.6)
            ))
            .aspectRatio(1, contentMode: .fit)
        }
    }
    static let gradientStart = Color(red: 239.0 / 255, green: 120.0 / 255, blue: 221.0 / 255)
    static let gradientEnd = Color(red: 239.0 / 255, green: 172.0 / 255, blue: 120.0 / 255)
}

fileprivate struct Card: View {
    var title = ""
    var body: some View {
        ZStack {
            CardPath()
            Text("Badge")
        }
        
    }
}

fileprivate struct PlayerPenaltiesBoard: View {
    var type: PersonType = .none;
    var body: some View {
        VStack {
            Button(action: {
                rs.persons[self.type].card = .passiveBlack
            }) {
                Card(title: "P")
            }
            Button(action: {
                rs.persons[self.type].card = .passiveYellow
            }) {
                Card(title: "P")
            }
            Button(action: {
                 rs.persons[self.type].card = .yellow
            }) {
                Card()
            }
        }
        
    }
}

struct CardsSwiftUIView: View {
    var body: some View {
        HStack(spacing: 0) {
            PlayerPenaltiesBoard(type: .left).frame(width: width / 2)
            PlayerPenaltiesBoard(type: .right).frame(width: width / 2)
        }
    }
}

struct CardsSwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        CardsSwiftUIView()
    }
}
