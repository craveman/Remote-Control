
public class RemoteServer: Codable {

  public static let empty = RemoteServer(ssid: "", ip: "", code: [])

  public let ssid: String
  public let ip: String
  public let code: [UInt8]

  public var description: String {
    return "RemoteServer(ssid:\(ssid),ip:\(ip),code:\(code))"
  }

  public init (ssid: String, ip: String, code: [UInt8]) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
  }

  public func isEmpty () -> Bool {
    return ssid == "" && ip == "" && code == []
  }
}
