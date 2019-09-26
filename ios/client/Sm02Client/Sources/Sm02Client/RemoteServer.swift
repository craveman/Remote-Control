
public struct RemoteServer: Codable {

  public let ssid: String
  public let ip: String
  public let code: [UInt8]

  public init (ssid: String, ip: String, code: [UInt8]) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
  }
}
