
import Foundation


public class Logger {

  let name: String
  let context: LogContext

  init (name: String, context: LogContext) {
    self.name = name
    self.context = context
  }

  public func debug (functionName: String = #function, line: Int = #line, _ format: String, _ arguments: Any...) {
    log(functionName, line, .DEBUG, format, arguments)
  }

  public func info (functionName: String = #function, line: Int = #line, _ format: String, _ arguments: Any...) {
    log(functionName, line, .INFO, format, arguments)
  }

  public func warn (functionName: String = #function, line: Int = #line, _ format: String, _ arguments: Any...) {
    log(functionName, line, .WARN, format, arguments)
  }

  public func error (functionName: String = #function, line: Int = #line, _ format: String, _ arguments: Any...) {
    log(functionName, line, .ERROR, format, arguments)
  }

  private func log (_ functionName: String, _ line: Int, _ level: LogLevel, _ format: String, _ arguments: [Any]?) {
    if context.logLevel.rawValue > level.rawValue {
      return
    }

    let logMessage = LogMessage(
        date: Date(),
        thread: Thread.current,
        function: functionName,
        line: line,
        level: level,
        loggerName: name,
        format: format,
        arguments: arguments?.map({ String(describing: $0) })
    )

    let logString = context.format(logMessage)
    print(logString)
  }
}
