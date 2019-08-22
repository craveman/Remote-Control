
import Foundation


public class LogContext {

  public static let ROOT = LogContext(
      parent: nil,
      dateFormatter: LogContext.DEFAULT_DATE_FORMATTER,
      outputLogFormat: LogContext.DEFAULT_OUTPUT_LOG_FORMAT
  )
  static let DEFAULT_DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS"
  static let DEFAULT_OUTPUT_LOG_FORMAT =
      "${faint:default}${DATE} " +
      "${:green}${LEVEL} " +
      "${faint:default}--- [${THREAD}] " +
      "${:cyan}${LOGGER_NAME} ${faint:default}: " +
      "${:default}${MESSAGE}"

  public var logLevel = LogLevel.INFO
  public let parent: LogContext?

  public var dateFormatter: String? {
    set { _dateFormatter = newValue.map { DateFormatter($0) } }
    get { return getDateFormatter()?.dateFormat }
  }
  public var outputLogFormat: String? {
    set { _outputLogFormat = newValue.map { OutputLogFormat.parse($0) } }
    get { return getOutputLogFormat()?.source }
  }

  var _dateFormatter: DateFormatter?
  var _outputLogFormat: OutputLogFormat?

  convenience public init (dateFormatter: String? = nil, outputLogFormat: String? = nil) {
    self.init(
        parent: LogContext.ROOT,
        dateFormatter: dateFormatter,
        outputLogFormat: outputLogFormat
    )
  }

  init (parent: LogContext?,
        dateFormatter: String?,
        outputLogFormat: String?
  ) {
    self.parent = parent
    self.dateFormatter = dateFormatter
    self.outputLogFormat = outputLogFormat
  }

  private func getDateFormatter () -> DateFormatter? {
    return _dateFormatter ?? parent?.getDateFormatter()
  }

  private func getOutputLogFormat () -> OutputLogFormat? {
    return _outputLogFormat ?? parent?.getOutputLogFormat()
  }
}

extension LogContext {

  func format (_ logMessage: LogMessage) -> String {
    guard let outputLogFormat = getOutputLogFormat() else {
      return ""
    }
    guard let dateFormatter = getDateFormatter() else {
      return ""
    }
    return outputLogFormat.format(logMessage, dateFormatter)
  }
}


fileprivate extension DateFormatter {

  convenience init (_ format: String) {
    self.init()
    dateFormat = format
  }
}
