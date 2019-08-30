
public protocol Loggable {

}

public extension Loggable {

  static var log: Logger {
    let thisType = type(of: self)
    let name = String(describing: thisType)
    return LoggerFactory.create(name)
  }

  var log: Logger {
    return Self.log
  }
}
