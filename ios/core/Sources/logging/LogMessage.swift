
import Foundation


struct LogMessage {

  let date: Date
  let thread: Thread
  let function: String
  let line: Int
  let level: LogLevel
  let loggerName: String
  let format: String
  let arguments: [String]?
}
