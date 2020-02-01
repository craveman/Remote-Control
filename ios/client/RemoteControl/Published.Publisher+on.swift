
import Foundation
import class Combine.AnyCancellable
import Dispatch


extension Published.Publisher {

  @discardableResult
  func on (change action: @escaping ((Value) -> Void)) -> AnyCancellable {
    return self
      .receive(on: RunLoop.main)
      .dropFirst(1)
      .sink(receiveValue: action)
  }
}
