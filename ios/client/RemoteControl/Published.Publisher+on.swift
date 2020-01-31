
import Foundation
import class Combine.AnyCancellable
import Dispatch


extension Published.Publisher {

  @discardableResult
  func on (change action: @escaping ((Value) -> Void)) -> AnyCancellable {
    return self
      .receive(on: DispatchQueue.global(qos: .background))
      .sink(receiveValue: action)
  }
}
