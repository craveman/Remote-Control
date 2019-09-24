
import Foundation
import CoreWLAN


var ssid: String {
  return CWWiFiClient.shared().interface(withName: nil)?.ssid() ?? ""
}
print("SSID: \(ssid)")
print("popa \(CWWiFiClient.shared().interface(withName: nil))")

@discardableResult
func shell(_ args: String...) -> Int32 {
    let task = Process()
    task.standardOutput = nil
    task.standardError = nil
    task.launchPath = "/usr/bin/env"
    task.arguments = args
    task.launch()
    task.waitUntilExit()
    return task.terminationStatus
}

func installed (_ command: String) -> Bool {
  return shell(command, "-h") == 0
}

func install (_ command: String) {
  if !installed("brew") || shell("brew", "install", command) != 0 {
    print("can't automatically install \(command), try to do it manually")
    exit(1)
  }
  print("successfully installed \(command)")
}

if !installed("qrencode") {
  install("qrencode")
}

let outputFile = FileManager.default.currentDirectoryPath + "/hello.png"
let url = "https://www.inspirationpoint.ru?access=ewogICJzc2lkIjogIngtY29tLWhvbWUiLAogICJpcCI6ICIxOTIuMTY4LjAuMSIsCiAgImNvZGUiOiBbNyw4LDEsOSwzXQp9"
if shell("qrencode", "-s", "6", "-o", outputFile, url) != 0 {
  print("can't generate QR-code into the file \(outputFile)")
  exit(1)
}
print("ok")


// import UIKit
// import Darwin


// func generateQRCode (from string: String) -> UIImage? {
//   let data = string.data(using: String.Encoding.ascii)

//   if let filter = CIFilter(name: "CIQRCodeGenerator") {
//     filter.setValue(data, forKey: "inputMessage")
//     let transform = CGAffineTransform(scaleX: 3, y: 3)

//     if let output = filter.outputImage?.transformed(by: transform) {
//       return UIImage(ciImage: output)
//     }
//   }

//   return nil
// }

// func saveImage (image: UIImage) -> Bool {
//   guard let data = image.jpegData(compressionQuality: 1) ?? image.pngData() else {
//     return false
//   }
//   guard let directory = try? FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false) as NSURL else {
//     return false
//   }
//   do {
//     try data.write(to: directory.appendingPathComponent("fileName.png")!)
//     return true
//   } catch {
//     print(error.localizedDescription)
//     return false
//   }
// }

// let image = generateQRCode(from: "Hacking with Swift is the best iOS coding tutorial I've ever read!")
// guard let qrCode = image else {
//   print("fuck")
//   exit(1)
// }
// saveImage(image: qrCode)
