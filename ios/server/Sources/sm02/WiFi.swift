
import CoreWLAN


enum WiFi {

  static var ssid: String? {
    return CWWiFiClient.shared().interface()?.ssid()
  }

  static var ip: String? {
    return Self.getIp()
  }

  static var info: (ssid: String, ip: String)? {
    guard let wifiSsid = ssid else {
      print("ERROR: couldn't get WiFi's SSID")
      return nil
    }
    guard let wifiIp = ip else {
      print("ERROR: couldn't get computer's IP address")
      return nil
    }
    return (ssid: wifiSsid, ip: wifiIp)
  }

  private static func getIp () -> String? {
    guard let interfaceName = CWWiFiClient.shared().interface()?.interfaceName else {
      return nil
    }

    var ifaddr : UnsafeMutablePointer<ifaddrs>?
    defer {
      freeifaddrs(ifaddr)
    }

    guard getifaddrs(&ifaddr) == 0 else {
      return nil
    }
    guard let firstAddr = ifaddr else {
      return nil
    }

    let interfaces = sequence(first: firstAddr, next: { $0.pointee.ifa_next })
        .map { $0.pointee }
        .filter {
          let addrFamily = $0.ifa_addr.pointee.sa_family
          return addrFamily == UInt8(AF_INET) || addrFamily == UInt8(AF_INET6)
        }
        .filter {
          let name = String(cString: $0.ifa_name)
          return name == interfaceName
        }

    if interfaces.isEmpty {
      return nil
    }
    guard let interface = interfaces.last else {
      return nil
    }

    var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
    getnameinfo(interface.ifa_addr, socklen_t(interface.ifa_addr.pointee.sa_len),
                &hostname, socklen_t(hostname.count),
                nil, socklen_t(0), NI_NUMERICHOST)

    return String(cString: hostname)
  }
}