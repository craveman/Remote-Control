
public final class LoggerFactory {

  private static var repository: [String: Logger] = [:]

  public static func create (_ name: String = #file) -> Logger {
    if let cached = repository[name] {
      return cached
    }

    let context = LogContext()
    let logger = Logger(name: name, context: context)
    repository[name] = logger
    return logger
  }
}
