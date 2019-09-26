
import Foundation
import struct Sm02Client.RemoteServer


guard let wifi = WiFi.info else {
  exit(1)
}

if !Shell.installed("qrencode") {
  print("WARN: command 'qrencode' is not installed.")
  print("      It needs for generating QR-codes.")
  print("      Press 'enter' to install it and continue.")
  let _ = readLine()

  Shell.install("qrencode")
  print("INFO: 'qrencode' was installed")
}

let thisServer = RemoteServer(
  ssid: wifi.ssid,
  ip: wifi.ip,
  code: [
    Int.random(in: 0 ..< 255),
    Int.random(in: 0 ..< 255),
    Int.random(in: 0 ..< 255),
    Int.random(in: 0 ..< 255),
    Int.random(in: 0 ..< 255)
  ]
)

let outputFile = FileManager.default.currentDirectoryPath + "/hello.png"
let url: String
switch thisServer.toURL() {
case let .success(urlString):
  url = urlString
case let .failure(.encodingJsonError(error)):
  exit(1)
case .failure(.invalidUrlEncoding):
  exit(1)
}
if Shell.exec("qrencode", "-s", "6", "-o", outputFile, url) != 0 {
  print("can't generate QR-code into the file \(outputFile)")
  exit(1)
}

print("This server object: \(thisServer)")
print("url: \(url)")
print("QR-code was writed to \(outputFile)")
Shell.exec("open", outputFile)
print("QR-code was opened in preview app...")
