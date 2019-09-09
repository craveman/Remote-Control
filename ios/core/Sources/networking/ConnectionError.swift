
public enum ConnectionError: Error {

  case parsingdError(String)
  case mandatoryResponseAbsent(request: Inbound)
}