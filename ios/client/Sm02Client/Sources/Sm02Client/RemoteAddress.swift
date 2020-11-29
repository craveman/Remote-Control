
public class RemoteAddress: Codable, CustomStringConvertible, Equatable {

  public static let empty = RemoteAddress(ssid: "", ip: "", code: [], name: "")

  public let name: String
  public let ssid: String
  public let ip: String
  public let code: [UInt8]

public static func == (lhs: RemoteAddress, rhs: RemoteAddress) -> Bool {
    return lhs.ssid == rhs.ssid &&
           lhs.ip == rhs.ip &&
           lhs.code == rhs.code
  }

  public var description: String {
    return "RemoteAddress(ssid:\(ssid),ip:\(ip),code:\(code),name:\(name))"
  }

  public init (ssid: String, ip: String, code: [UInt8], name: String) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
    self.name = name
  }

  public func isEmpty () -> Bool {
    return self === RemoteAddress.empty
  }
}
