
import NIO


public enum ConnectionEvent {

  case pingCatched(serverAddress: SocketAddress)

  case unknownCommand(String)
  case wrongVerificationCode
  case remoteControllerAlreadyExists
  case successfulRegistration(server: SocketAddress)
  case connectionReadTimeout
}
