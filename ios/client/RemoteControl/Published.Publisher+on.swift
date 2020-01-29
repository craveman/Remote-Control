
import Foundation
import class Combine.AnyCancellable
import Dispatch


extension Published.Publisher {

  @discardableResult
  func on (change action: @escaping ((Value) -> Void)) -> AnyCancellable {
    return self
      .receive(on: DispatchQueue.global(qos: .background))
      .dropFirst(1) // to skip the initial value on the moment of the subscription
      .sink(receiveValue: action)
  }
}
