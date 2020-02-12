
import class Sm02Client.RemoteAddress
import class Foundation.JSONEncoder
import struct Foundation.Data


extension RemoteAddress {

  static let URL = "https://www.inspirationpoint.ru?access="

  func toURL () -> Result<String, EncodingError> {
    let encoder = JSONEncoder()
    let jsonData: Data
    do {
      jsonData = try encoder.encode(self)
    } catch {
      return .failure(.encodingJsonError(error))
    }

    let base64Encoded = jsonData.base64EncodedString()

    guard let urlEncoded = base64Encoded.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) else {
      return .failure(.invalidUrlEncoding)
    }

    let result = Self.URL + urlEncoded
    return .success(result)
  }

  enum EncodingError: Error {

    case encodingJsonError(Error)
    case invalidUrlEncoding
  }
}
