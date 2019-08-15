
struct Font {

  static func parse (_ string: String) -> String? {
    guard string.hasPrefix("${") && string.hasSuffix("}") else {
      return nil
    }

    let from = string.firstIndex(of: "{")
      .map { string.index(after: $0) }!

    let to = string.lastIndex(of: "}")!

    let substring = string[from..<to]
    let tokens = substring.split(separator: ":", maxSplits: 2)

    switch tokens.count {
    case 1:
      let token = String(tokens[0])
      if let effect = FontEffect.from(token) {
        return "\u{001B}[\(effect.rawValue);\(FontColor.default.rawValue)m"
      } else if let color = FontColor.from(token) {
        return "\u{001B}[\(FontEffect.normal.rawValue);\(color.rawValue)m"
      } else {
        return nil
      }
    case 2:
      guard let effect = FontEffect.from(String(tokens[0])) else {
        return nil
      }
      guard let color = FontColor.from(String(tokens[1])) else {
        return nil
      }
      return "\u{001B}[\(effect.rawValue);\(color.rawValue)m"
    default:
      return nil
    }
  }
}


fileprivate enum FontEffect: Int, ParseFromString {

  case normal = 0
  case bold = 1
  case faint = 2
  case italic = 3
  case underline = 4
  case slowBlink = 5
  case rapidBlink = 6

  var description: String {
    switch self {
    case .normal:
      return "normal"
    case .bold:
      return "bold"
    case .faint:
      return "faint"
    case .italic:
      return "italic"
    case .underline:
      return "underline"
    case .slowBlink:
      return "slowblink"
    case .rapidBlink:
      return "rapidblink"
    }
  }
}


fileprivate enum FontColor: Int, ParseFromString {

  case black = 30
  case red = 31
  case green = 32
  case yellow = 33
  case blue = 34
  case magenta = 35
  case cyan = 36
  case white = 37
  case `default` = 39

  var description: String {
    switch self {
    case .black:
      return "black"
    case .red:
      return "red"
    case .green:
      return "green"
    case .yellow:
      return "yellow"
    case .blue:
      return "blue"
    case .magenta:
      return "magenta"
    case .cyan:
      return "cyan"
    case .white:
      return "white"
    case .`default`:
      return "default"
    }
  }
}


fileprivate protocol ParseFromString: CaseIterable, CustomStringConvertible {

}


fileprivate extension ParseFromString {

  static func from (_ string: String) -> Self? {
    let normal = string
      .trimmingCharacters(in: .whitespacesAndNewlines)
      .lowercased()

    for myCase in Self.allCases {
      if normal == myCase.description {
        return myCase
      }
    }
    return nil
  }
}
