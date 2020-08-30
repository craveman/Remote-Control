
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
    let message = unwrapInboundIn(data)

    var buffer = message.data
    guard let message = buffer.getString(at: 0, length: SEARCH_MESSAGE.utf8.count) else {
      print("LookupMessagesHandler - WARN: message doesn't have enough length")
      return
    }

    if message != SEARCH_MESSAGE {
      print("LookupMessagesHandler - WARN: the unknown inbound message - {}", message)
      return
    }

    let remoteAddress = RemoteAddress(
      ssid: "",
      ip: "\(message.remoteAddress)",
      code: [0,0,0,0,0]
    )
    if servers.fire(it: remoteAddress) == false {
      print("LookupMessagesHandler - WARN: there is no messages processor for {} client", context.remoteAddress!)
      return
    }
  }
}
