
class LabelDispatch {

  private static var nextId = 0

  static func get () -> String {
    let id = nextId
    nextId += 1
    return "com.xxlabaza.threads.dispatch_queue.\(id)"
  }
}