
import Foundation


public class LogContext {

  public static let ROOT = LogContext(
    parent: nil,
    logLevel: .INFO,
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

  private var _logLevel: AskParentField<LogLevel>
  private var _dateFormatter: AskParentField<DateFormatter>
  private var _outputLogFormat: AskParentField<OutputLogFormat>

  public let parent: LogContext?

  public var logLevel: LogLevel {
    set { _logLevel.value = newValue }
    get { return _logLevel.value ?? .OFF }
  }
  public var dateFormatter: String {
    set { _dateFormatter.value = DateFormatter(newValue) }
    get { return _dateFormatter.value?.dateFormat ?? "" }
  }
  public var outputLogFormat: String {
    set { _outputLogFormat.value = OutputLogFormat.parse(newValue) }
    get { return _outputLogFormat.value?.source ?? "" }
  }

  convenience public init (logLevel: LogLevel? = nil,
                           dateFormatter: String? = nil,
                           outputLogFormat: String? = nil
  ) {
    self.init(
      parent: LogContext.ROOT,
      logLevel: logLevel,
      dateFormatter: dateFormatter,
      outputLogFormat: outputLogFormat
    )
  }

  public init (parent: LogContext? = LogContext.ROOT,
               logLevel: LogLevel? = nil,
               dateFormatter: String? = nil,
               outputLogFormat: String? = nil
  ) {
    self.parent = parent

    _logLevel = AskParentField<LogLevel>(
      parent: parent,
      path: \LogContext._logLevel,
      value: logLevel
    )
    _dateFormatter = AskParentField<DateFormatter>(
      parent: parent,
      path: \LogContext._dateFormatter
    )
    _outputLogFormat = AskParentField<OutputLogFormat>(
      parent: parent,
      path: \LogContext._outputLogFormat
    )

    if let dateFormatterString = dateFormatter {
      self.dateFormatter = dateFormatterString
    }
    if let outputLogFormatString = outputLogFormat {
      self.outputLogFormat = outputLogFormatString
    }
  }
}

extension LogContext {

  func format (_ logMessage: LogMessage) -> String {
    guard let outputLogFormat = _outputLogFormat.value else {
      return ""
    }
    guard let dateFormatter = _dateFormatter.value else {
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

fileprivate struct AskParentField<T> {

  private let parent: LogContext?
  private let path: ReferenceWritableKeyPath<LogContext, AskParentField<T>>

  private var _value: T?

  var value: T? {
    set { _value = newValue }
    get { return _value ?? parent?[keyPath: path].value}
  }

  init (parent: LogContext?, path: ReferenceWritableKeyPath<LogContext, AskParentField<T>>, value: T? = nil) {
    self.parent = parent
    self.path = path
    self._value = value
  }
}
