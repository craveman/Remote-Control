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
  var addAction: () -> Void = {}
  var resetAction: () -> Void = {}
  var body: some View {
    ZStack {
      CardPath(color: self.color)
      dinFont(Text(self.title), UIGlobals.popupContentFontSize)
    }
    .gesture(resetGesture.onEnded { _ in
      print("Card::resetGesture:action")
      self.resetAction()
    }).highPriorityGesture(addCardGesture.onEnded { _ in
      print("Card::addCardGesture:action")
      self.addAction()
    })
    
    
  }
}

fileprivate struct PlayerPenaltiesBoard: View {
  var type: PersonType = .none;
  @EnvironmentObject var settings: FightSettings
  
  func getColor(_ expect: StatusCard, _ test: StatusCard) -> Color {
    return expect == test ? Color.yellow : Color.red
  }
  
  func getCurrentCard(_ isPCard: Bool) -> StatusCard {
    switch self.type {
    case .left:
      return isPCard ? self.settings.leftCardP : self.settings.leftCard
    case .right:
      return isPCard ? self.settings.rightCardP : self.settings.rightCard
    default:
      return .none
    }
  }
  
  
  func getCard() -> StatusCard {
    return self.getCurrentCard(false) == .none ? StatusCard.yellow : StatusCard.red
  }
  
  func getPCard() -> StatusCard {
    return self.getCurrentCard(true) == .passiveNone ? StatusCard.passiveYellow : StatusCard.passiveRed
   }
  
  var body: some View {
    VStack {
      Card(title: "P", color: .black, addAction: {
        rs.persons[self.type].card = .passiveBlack
      }, resetAction: {
         rs.persons[self.type].card = .passiveNone
      })
      Card(title: "P", color: self.getColor(.passiveNone, getCurrentCard(true)), addAction: {
        let nextCard = self.getPCard()
        // todo: if red is set - add 1 point
        if nextCard == StatusCard.passiveRed {
          rs.persons[self.type].score = rs.persons[self.type].score + 1
        }
        rs.persons[self.type].card = nextCard
      }, resetAction: {
         rs.persons[self.type].card = .passiveNone
      })
      Card(title: "", color: self.getColor(.none, getCurrentCard(false)), addAction: {
        let nextCard = self.getCard()
        // todo: if red is set - add 1 point
        if nextCard == StatusCard.red {
          rs.persons[self.type].score = rs.persons[self.type].score + 1
        }
        rs.persons[self.type].card = nextCard
      }, resetAction: {
        rs.persons[self.type].card = .none
      })
    }
    
  }
}
fileprivate let holdLongPressDuration = 0.95
fileprivate let resetGesture = LongPressGesture(minimumDuration: holdLongPressDuration)
fileprivate let addCardGesture = TapGesture(count: 1)
struct CardsSwiftUIView: View {
  
 
  var body: some View {
    VStack {
      HStack(spacing: 0) {
         PlayerPenaltiesBoard(type: .left).frame(width: width / 2)
         PlayerPenaltiesBoard(type: .right).frame(width: width / 2)
       }
      dinFont(Text("* hold to reset"))
      
    }
   
  }
}

struct CardsSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    CardsSwiftUIView()
  }
}
