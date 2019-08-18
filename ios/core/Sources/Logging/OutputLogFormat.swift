
import Foundation


struct OutputLogFormat {

  static func parse (_ format: String) -> OutputLogFormat {
    var tokens: [Any] = []
    var string = ""

    var iterator = format.makeIterator()
    while let character = iterator.next() {
      if character != "$" {
        string.append(character)
        continue
      }

      var token = "$"
      while let item = iterator.next() {
        token.append(item)
        if item == "}" {
          break
        }
      }

      if let font = Font.parse(token) {
        string.append(font.description)
        continue
      }

      guard let placeholder = Placeholder(rawValue: token) else {
        string.append(token)
        continue
      }
      tokens.append(string)
      string.removeAll(keepingCapacity: true)
      tokens.append(placeholder)
    }

    if !string.isEmpty {
      tokens.append(string)
      string.removeAll(keepingCapacity: true)
    }

    return OutputLogFormat(source: format, tokens: tokens)
  }

  let source: String
  let tokens: [Any]

  func format (_ logMessage: LogMessage, _ dateFormatter: DateFormatter) -> String {
    var result = ""
    for token in tokens {
      if let string = token as? String {
        result.append(string)
        continue
      }

      guard let placeholder = token as? Placeholder else {
        print("ERROR: unknown token \(token)")
        return result
      }

      switch placeholder {
      case .date:
        let string = dateFormatter.string(from: logMessage.date)
        result.append(string)
      case .thread:
        let threadName = getThreadName()
        result.append(threadName)
      case .function:
        result.append(logMessage.function)
      case .lineNumber:
        let string = String(logMessage.line)
        result.append(string)
      case .level:
        let string = logMessage.level.getAlignedString()
        result.append(string)
      case .loggerName:
        result.append(logMessage.loggerName)
      case .message:
        let message = MessageFormat.format(logMessage.format, logMessage.arguments)
        result.append(message)
      }
    }
    return result
  }

  private func getThreadName () -> String {
    let threadLabel = __dispatch_queue_get_label(nil)
    if let string = String(cString: threadLabel, encoding: .utf8) {
      return string
    } else if Thread.current.isMainThread {
      return "main"
    }
    return "unknown"
  }
}

private enum Placeholder: String {

  case date = "${DATE}"
  case thread = "${THREAD}"
  case function = "${FUNCTION}"
  case lineNumber = "${LINE_NUMBER}"
  case level = "${LEVEL}"
  case loggerName = "${LOGGER_NAME}"
  case message = "${MESSAGE}"
}
