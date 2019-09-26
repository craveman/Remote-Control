
public struct RemoteServer: Codable {

  public let ssid: String
  public let ip: String
  public let code: [Int]

  public init (ssid: String, ip: String, code: [Int]) {
    self.ssid = ssid
    self.ip = ip
    self.code = code
  }
}
