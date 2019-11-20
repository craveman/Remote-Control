
public class RemoteAddress: Codable, CustomStringConvertible {

  public static let empty = RemoteAddress(ssid: "", ip: "", code: [])

  public let ssid: String
  public let ip: String
  public let code: [UInt8]

  public var description: String {
    return "RemoteAddress(ssid:\(ssid),ip:\(ip),code:\(code))"
  }

  public init (ssid: String, ip: String, code: [UInt8]) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
  }

  public func isEmpty () -> Bool {
    return self === RemoteAddress.empty
  }
}
