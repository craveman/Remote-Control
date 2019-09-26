
import Foundation


enum Shell {

  @discardableResult
  static func exec (_ args: String...) -> Int32 {
      let task = Process()
      task.standardOutput = nil
      task.standardError = nil
      task.launchPath = "/usr/bin/env"
      task.arguments = args
      task.launch()
      task.waitUntilExit()
      return task.terminationStatus
  }

  static func installed (_ command: String) -> Bool {
    return exec(command, "-h") == 0
  }

  static func install (_ command: String) {
    if !installed("brew") || exec("brew", "install", command) != 0 {
      print("can't automatically install \(command), try to do it manually")
      exit(1)
    }
    print("successfully installed \(command)")
  }
}