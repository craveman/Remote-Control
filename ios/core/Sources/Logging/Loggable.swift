
public protocol Loggable {

}

public extension Loggable {

  var log: Logger {
    let thisType = type(of: self)
    let name = String(describing: thisType)
    return LoggerFactory.create(name)
  }
}
