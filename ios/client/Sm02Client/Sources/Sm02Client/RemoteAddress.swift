
public class RemoteAddress: Codable, CustomStringConvertible, Equatable {

  public static let empty = RemoteAddress(ssid: "", ip: "", port: 0, code: [])

  public let ssid: String
  public let ip: String
  public let port: UInt16
  public let code: [UInt8]

public static func == (lhs: RemoteAddress, rhs: RemoteAddress) -> Bool {
    return lhs.ssid == rhs.ssid &&
           lhs.ip == rhs.ip &&
           lhs.port == rhs.port &&
           lhs.code == rhs.code
  }

  public var description: String {
    return "RemoteAddress(ssid:\(ssid),ip:\(ip),code:\(code))"
  }

  public init (ssid: String, ip: String, code: [UInt8]) {
    self.ssid = ssid
    self.ip = ip
    self.port = 21074
    self.code = code
  }
  
  public init (ssid: String, ip: String, port: UInt16, code: [UInt8]) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
    self.port = port
  }

  public func isEmpty () -> Bool {
    return self === RemoteAddress.empty
  }
}
