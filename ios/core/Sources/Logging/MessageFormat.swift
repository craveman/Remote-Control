
final class MessageFormat {

  static func format (_ format: String, _ arguments: [String]?) -> String {
    var result = ""

    var argumentsIterator = arguments?.makeIterator()
    var iterator = format.makeIterator()
    while let character = iterator.next() {
      if character != "{" {
        result.append(character)
        continue
      }

      guard let nextCharacter = iterator.next() else {
        result.append(character)
        continue
      }

      if nextCharacter != "}" {
        result.append(character)
        result.append(nextCharacter)
        continue
      }

      if let value = argumentsIterator?.next() {
        result.append(value)
      }
    }
    return result
  }
}
