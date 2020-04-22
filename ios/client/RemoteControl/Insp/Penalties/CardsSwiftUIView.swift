//
//  CardsSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 23.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct CardPathParameters {
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
        let xScale: CGFloat = 0.85
        let xOffset = (width * (1.0 - xScale)) / 2.0
        width *= xScale
        path.move(
          to:  CGPoint(
            x: xOffset + width * CardPathParameters.startPoint.useWidth * CardPathParameters.startPoint.xFactors,
            y: height * CardPathParameters.startPoint.useHeight * CardPathParameters.startPoint.yFactors
          )
        )
        
        CardPathParameters.points.forEach {
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
  var textColor = primaryColor
  var addAction: () -> Void = {}
  var resetAction: () -> Void = {}
  @State var isActive = false
  
  func thenDeactivate (_ timeout: Double = 1.1) -> Void {
    withDelay({
      self.isActive = false
    }, timeout)
  }
  var body: some View {
    ZStack {
      CardPath(color: self.color)
      dinFont(Text(self.title).foregroundColor(self.textColor), UIGlobals.popupContentFontSize).offset(x: -9, y: -18)
    }
    .gesture(resetGesture.onEnded { _ in
      guard !self.isActive else {
        return
      }
      print("Card::resetGesture:action")
      self.resetAction()
      Vibration.notification()
      self.isActive = true
      self.thenDeactivate()
    }).highPriorityGesture(addCardGesture.onEnded { _ in
      guard !self.isActive else {
        return
      }
      print("Card::addCardGesture:action")
      self.addAction()
      Vibration.on()
      self.isActive = true
      self.thenDeactivate()
    })
  }
}

fileprivate struct PlayerPenaltiesBoard: View {
  var type: PersonType = .none
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
  
  func setCard(_ card: StatusCard) -> Void {
    rs.persons[self.type].card = card
  }
  
  func getOponentType() -> PersonType {
    switch self.type {
    case .left:
      return .right
    case .right:
      return .left
    default:
      return .none
    }
  }
  
  func getOponent() -> RemoteService.PersonsManagement.Person? {
    var op: RemoteService.PersonsManagement.Person? = nil
    switch self.getOponentType() {
    case .left:
      op = rs.persons.left
    case .right:
      op = rs.persons.right
    default:
      break
    }
    return op
  }
  
  func oponentScoreMaxReached() -> Bool {
    let op = getOponentType()
    switch op {
    case .left:
      return settings.leftScore >= MAX_SCORE
    case .right:
      return settings.rightScore >= MAX_SCORE
    default:
      return false
    }
  }
  
  func oponentGetsPoint() {
    let op = getOponent()
    guard op != nil else {
      return
    }
    if (op!.score < MAX_SCORE) {
      op!.score += 1
    } else {
      Vibration.warning()
    }
    
  }
  
  var body: some View {
    VStack {
      Card(title: "P", color: .black, textColor: .white, addAction: {
        self.setCard(.passiveBlack)
      }, resetAction: {
        self.setCard(.passiveNone)
      })
      Card(title: "P", color: self.getColor(.passiveNone, getCurrentCard(true)), addAction: {
        let nextCard = self.getPCard()
        
        if nextCard == StatusCard.passiveRed {
          self.oponentGetsPoint()
        }
        self.setCard(nextCard)
      }, resetAction: {
        self.setCard(.passiveNone)
      })
      Card(title: "", color: self.getColor(.none, getCurrentCard(false)), addAction: {
        let nextCard = self.getCard()
        
        if nextCard == StatusCard.red {
          self.oponentGetsPoint()
        }
        self.setCard(nextCard)
      }, resetAction: {
        self.setCard(.none)
      })
    }
    
  }
}
fileprivate let holdLongPressDuration = 0.95
fileprivate let resetGesture = LongPressGesture(minimumDuration: holdLongPressDuration)
fileprivate let addCardGesture = TapGesture(count: 1)
struct CardsSwiftUIView: View {
  
  @EnvironmentObject var settings: FightSettings
  
  var body: some View {
    VStack {
      HStack(spacing: 0) {
        PlayerPenaltiesBoard(type: .left).frame(width: width / 2)
        PlayerPenaltiesBoard(type: .right).frame(width: width / 2)
      }
      dinFont(Text("* hold to reset"), UIGlobals.appSmallerFontSize)
        .padding([.vertical], 5)
        .frame(width: width)
        .foregroundColor(.white)
        .background(UIGlobals.headerBackground_SUI)
      
    }
    //    .background(UIGlobals.cardBoardBackground)
    
  }
}

struct CardsSwiftUIView_Previews: PreviewProvider {
  static let settings = FightSettings()
  static var previews: some View {
    CardsSwiftUIView().onAppear(perform: {
      CardsSwiftUIView_Previews.settings.leftScore = 50
    }
    ).environmentObject(CardsSwiftUIView_Previews.settings)
  }
}
