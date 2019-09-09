
import NIO


public enum ConnectionEvent {

  case pingCatched(remoteHost: String)
  case connectionReadTimeout
}
