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
    let useWidth: (CGFloat)
    let xFactors: (CGFloat)
    let useHeight: (CGFloat)
    let yFactors: (CGFloat)
  }
  
  static let adjustment: CGFloat = 0.085
  
  static let startPoint = Segment(
    useWidth:  (1.00),
    xFactors:  (0.30),
    useHeight: (1.00),
    yFactors:  (0.20)
  )
  
  static let points = [
    Segment(
      useWidth:  (1.00),
      xFactors:  (0.70),
      useHeight: (1.00),
      yFactors:  (0.20)
    ),
    Segment(
      useWidth:  (1.00),
      xFactors:  (0.70),
      useHeight: (1.00),
      yFactors:  (0.80)
    ),
    Segment(
      useWidth:  (1.00),
      xFactors:  (0.30),
      useHeight: (1.00),
      yFactors:  (0.8)
    )
  ]
}

fileprivate struct CardPath: View {
  var color = primaryColor
  var body: some View {
    GeometryReader { geometry in
      Path { path in
        var width: CGFloat = min(geometry.size.width, geometry.size.height)
        let height = width
        let xScale: CGFloat = 0.832
        let xOffset = (width * (1.0 - xScale)) / 2.0
        width *= xScale
        path.move(
          to:  CGPoint(
            x: xOffset + width * HexagonParameters.startPoint.useWidth * HexagonParameters.startPoint.xFactors,
            y: height * HexagonParameters.startPoint.useHeight * HexagonParameters.startPoint.yFactors
          )
        )
        
        HexagonParameters.points.forEach {
          path.addLine(
            to: .init(
              x: xOffset + width * $0.useWidth * $0.xFactors,
              y: height * $0.useHeight * $0.yFactors
            )
          )
        }
      }
      .fill(self.color)
      .aspectRatio(1, contentMode: .fit)
    }
  }
  static let gradientStart = Color(red: 239.0 / 255, green: 120.0 / 255, blue: 221.0 / 255)
  static let gradientEnd = Color(red: 239.0 / 255, green: 172.0 / 255, blue: 120.0 / 255)
}

fileprivate struct Card: View {
  var title = ""
  var color = primaryColor
  var body: some View {
    ZStack {
      CardPath(color: self.color)
      dinFont(Text(self.title), UIGlobals.popupContentFontSize)
    }
    
  }
}

fileprivate struct PlayerPenaltiesBoard: View {
  var type: PersonType = .none;
  @EnvironmentObject var settings: FightSettings
  
  func getColor() -> Color {
    switch self.type {
    case .left:
      return self.settings.leftCard == .none ? Color.yellow : Color.red
    case .right:
        return self.settings.rightCard == .none ? Color.yellow : Color.red
    default:
      return .green
    }
  }
  
  
  func getCard() -> StatusCard {
    switch self.type {
    case .left:
      return self.settings.leftCard == .none ? StatusCard.yellow : StatusCard.red
    case .right:
        return self.settings.rightCard == .none ? StatusCard.yellow : StatusCard.red
    default:
      return .none
    }
  }
  
  func getPCard() -> StatusCard {
     switch self.type {
     case .left:
       return self.settings.leftCard == .none ? StatusCard.passiveYellow : StatusCard.passiveRed
     case .right:
         return self.settings.rightCard == .none ? StatusCard.passiveYellow : StatusCard.passiveRed
     default:
       return .none
     }
   }
  
  var body: some View {
    VStack {
      Button(action: {
        rs.persons[self.type].card = .passiveBlack
      }) {
        Card(title: "P")
      }
      Button(action: {
        let nextCard = self.getPCard()
        rs.persons[self.type].card = nextCard
      }) {
        Card(title: "P", color: self.getColor())
      }
      Button(action: {
        let nextCard = self.getCard()
        rs.persons[self.type].card = nextCard
      }) {
        Card(title: "", color: self.getColor())
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
