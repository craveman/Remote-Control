
import NIO
import Foundation


final class LookupMessagesHandler: ChannelInboundHandler {

  typealias Container = Singletons & ChannelHandlerFactory
  typealias InboundIn = AddressedEnvelope<ByteBuffer>

  private static let SEARCH_MESSAGE: String = "SRCH"

  let servers: HandlersManager<RemoteAddress>

  init (container: Container) {
    servers = container.serversManager
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let envelope = unwrapInboundIn(data)
    print("LookupMessagesHandler - INFO: an inbound message from \(envelope.remoteAddress.ipAddress!)")

    let buffer = envelope.data
    guard let string = buffer.getString(at: 0, length: LookupMessagesHandler.SEARCH_MESSAGE.utf8.count) else {
      print("LookupMessagesHandler - WARN: message doesn't have enough length")
      return
    }

    print("LookupMessagesHandler - INFO: the inbound message is '\(string)'")
    if string != LookupMessagesHandler.SEARCH_MESSAGE {
      print("LookupMessagesHandler - WARN: the unknown inbound message - \(string)")
      return
    }

    let remoteAddress = RemoteAddress(
      ssid: "",
      ip: envelope.remoteAddress.ipAddress!,
      code: [0,0,0,0,0],
      name: envelope.remoteAddress.ipAddress!
    )

    print("LookupMessagesHandler - INFO: the new created remote address is \(remoteAddress)")
    if servers.fire(it: remoteAddress) == false {
      print("LookupMessagesHandler - WARN: there is no messages processor for \(context.remoteAddress!) client")
      return
    }
  }
}
