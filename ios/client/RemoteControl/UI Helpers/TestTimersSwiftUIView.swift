//
//  TestTimersSwiftUIView.swift
//  RemoteControl
//
//  Created by Sergei Andreev on 30.11.2019.
//  Copyright © 2019 Sergei Andreev. All rights reserved.
//

import SwiftUI

struct TestTimersSwiftUIView: View {
  let times: [UInt32] = [600_001,600_000,599_999, 185_000, 180_999, 180_500, 180_499, 18_765, 18_234, 1_899, 1_895, 1_892, 1_111, 999, 111, 0]
  var body: some View {
    HStack{
      Spacer()
      VStack(alignment: .trailing){
        Text("input in ms")
        ForEach(times, id: \.self) {
          Text("\($0)")
        }
      }
      Spacer()
      VStack(alignment: .trailing){
        Text("w/o ms")
        ForEach(times, id: \.self) {
          Text(getTimeString($0))
        }
      }
      Spacer()
      VStack(alignment: .trailing){
        Text("w/ ms")
        ForEach(times, id: \.self) {
          Text(getTimeString($0, true))
        }
      }
      Spacer()
    }
    
  }
}

struct TestTimersSwiftUIView_Previews: PreviewProvider {
  static var previews: some View {
    TestTimersSwiftUIView()
  }
}
