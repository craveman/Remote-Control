
public enum ConnectionError: Error {

  case connectionTimeout(Int)
  case parsingdError(String)
  case mandatoryResponseAbsent(request: Inbound)
  case connectionRefused
  case responseTimeout(Int)
}
