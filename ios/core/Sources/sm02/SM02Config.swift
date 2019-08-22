
public struct SM02Config {

  public internal(set) var udpBroadcast: UdpBroadcastConfig
  public internal(set) var tcpServer: TcpServerConfig

  public init (udpBroadcast: UdpBroadcastConfig = UdpBroadcastConfig(),
               tcpServer: TcpServerConfig = TcpServerConfig()
  ) {
    self.udpBroadcast = udpBroadcast
    self.tcpServer = tcpServer
  }

  public func with<T> (_ path: ReferenceWritableKeyPath<SM02Config, T>, _ value: T) -> SM02Config {
    let copy = self
    copy[keyPath: path] = value
    return copy
  }

  public struct UdpBroadcastConfig {

    public internal(set) var host: String
    public internal(set) var port: Int
    public internal(set) var secondsInterval: Int

    public init (host: String = "127.0.0.1",
                 port: Int = 21075,
                 secondsInterval: Int = 2
    ) {
      self.host = host
      self.port = port
      self.secondsInterval = secondsInterval
    }
  }

  public struct TcpServerConfig {

    public internal(set) var host: String
    public internal(set) var port: Int

    public init (host: String = "127.0.0.1", port: Int = 21074) {
      self.host = host
      self.port = port
    }
  }
}
