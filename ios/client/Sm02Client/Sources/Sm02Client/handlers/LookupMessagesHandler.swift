
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

    let buffer = envelope.data
    guard let string = buffer.getString(at: 0, length: LookupMessagesHandler.SEARCH_MESSAGE.utf8.count) else {
      print("LookupMessagesHandler - WARN: message doesn't have enough length")
      return
    }

    if string != LookupMessagesHandler.SEARCH_MESSAGE {
      print("LookupMessagesHandler - WARN: the unknown inbound message - {}", string)
      return
    }

    let remoteAddress = RemoteAddress(
      ssid: "",
      ip: "\(envelope.remoteAddress)",
      code: [0,0,0,0,0]
    )
    if servers.fire(it: remoteAddress) == false {
      print("LookupMessagesHandler - WARN: there is no messages processor for {} client", context.remoteAddress!)
      return
    }
  }
}
