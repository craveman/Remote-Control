
public enum LogLevel: Int {

  case DEBUG = 1
  case INFO = 2
  case WARN = 3
  case ERROR = 4
  case OFF = 99
}

extension LogLevel {

  func getAlignedString () -> String {
    switch self {
    case .DEBUG:
      return "DEBUG"
    case .INFO:
      return " INFO"
    case .WARN:
      return " WARN"
    case .ERROR:
      return "ERROR"
    case .OFF:
      return "  OFF"
    }
  }
}
