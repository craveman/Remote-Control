
import Foundation
import struct Sm02Client.RemoteAddress


guard let wifi = WiFi.info else {
  exit(1)
}

if !Shell.installed("qrencode") {
  print(" WARN: command 'qrencode' is not installed.")
  print("       It needs for generating QR-codes.")
  print("       Press 'enter' to install it and continue.")
  let _ = readLine()

  Shell.install("qrencode")
  print(" INFO: 'qrencode' was installed")
}

let thisServer = RemoteAddress(
  ssid: wifi.ssid,
  ip: wifi.ip,
  code: [
    UInt8.random(in: 0 ..< 255),
    UInt8.random(in: 0 ..< 255),
    UInt8.random(in: 0 ..< 255),
    UInt8.random(in: 0 ..< 255),
    UInt8.random(in: 0 ..< 255)
  ]
)

let outputFile = FileManager.default.currentDirectoryPath + "/hello.png"
let url: String
switch thisServer.toURL() {
case let .success(urlString):
  url = urlString
case let .failure(.encodingJsonError(error)):
  print("ERROR: JSON encoding server object error.")
  print("       \(error)")
  exit(1)
case .failure(.invalidUrlEncoding):
  print("ERROR: unsuccess URL encoding.")
  exit(1)
}
if Shell.exec("qrencode", "-s", "6", "-o", outputFile, url) != 0 {
  print("can't generate QR-code into the file \(outputFile)")
  exit(1)
}

let server = Sm02Server(
  code: thisServer.code,
  onMessage: messageHandler,
  onEvent: eventHandler
)
server.start()

print(" INFO: This server object: \(thisServer)")
print(" INFO: url: \(url)")
print(" INFO: QR-code was writed to \(outputFile)")
Shell.exec("open", outputFile)
print(" INFO: QR-code was opened in preview app...")

try! server.channel?.closeFuture.wait()
