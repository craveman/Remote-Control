//
//  ServerAccess.swift
//  RemoteControl
//
//  Created by Artem Labazin on 09/09/2019.
//  Copyright © 2019 Artem Labazin. All rights reserved.
//

import struct Foundation.Data
import struct Foundation.URL
import struct Foundation.URLComponents
import class Foundation.JSONDecoder
import class Sm02Client.RemoteAddress


extension RemoteAddress {

  static func parse (url: String) -> Result<RemoteAddress, ParsingError> {
    let trimmedUrl = url.trimmingCharacters(in: .whitespacesAndNewlines)
    guard let components = URLComponents(string: trimmedUrl) else {
      return .failure(.invalidUrl)
    }
    return parse(urlComponents: components)
  }

  static func parse (url: URL) -> Result<RemoteAddress, ParsingError> {
    guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false) else {
      return .failure(.invalidUrl)
    }
    return parse(urlComponents: components)
  }

  static func parse (urlComponents: URLComponents) -> Result<RemoteAddress, ParsingError> {
    guard let queryItems = urlComponents.queryItems else {
      return .failure(.noQueryParameter)
    }
    guard let access = queryItems.first(where: { $0.name.lowercased() == "access" })?.value else {
      return .failure(.noQueryParameter)
    }
    return parse(parameter: access)
  }

  static func parse (parameter: String) -> Result<RemoteAddress, ParsingError> {
    guard let urlDecoded = parameter.removingPercentEncoding else {
      return .failure(.invalidUrlEncoding)
    }
    guard let base64DataDecoded = Data(base64Encoded: urlDecoded) else {
      return .failure(.invalidBase64Encoding)
    }

    let decoder = JSONDecoder()
    do {
      let result = try decoder.decode(RemoteAddress.self, from: base64DataDecoded)
      return .success(result)
    } catch {
      return .failure(.parsingJsonError(error))
    }
  }

  enum ParsingError: Error {

    case invalidUrl
    case noQueryParameter
    case invalidUrlEncoding
    case invalidBase64Encoding
    case parsingJsonError(Error)
  }
}
