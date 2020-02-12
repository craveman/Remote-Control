
import NIO
import Sm02Client


final class AuthorizationHandler: ChannelInboundHandler {

  typealias InboundIn = Outbound
  typealias OutboundOut = Inbound

  let code: [UInt8]
  var authorized = false

  init (_ code: [UInt8]) {
    self.code = code
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let outbound = unwrapInboundIn(data)
    guard let response = handle(request: outbound) else {
      context.fireChannelRead(data)
      return
    }
    let out = self.wrapOutboundOut(response)
    context.writeAndFlush(out, promise: nil)
  }

  private func handle (request: Outbound) -> Inbound? {
    if authorized {
      if case .authenticate(_, _, _, _) = request {
        print(" WARN: remote control already authorized")
        return .authentication(status: .alreadyRegistered)
      }
      return nil
    }

    guard case let .authenticate(_, code, name, version) = request else {
      print(" WARN: access forbidden")
      return .genericResponse(status: 0x02, request: request.tag)
    }

    if code != self.code {
      print(" WARN: invalid access code")
      return .authentication(status: .wrongAuthenticationCode)
    }

    authorized = true
    print(" INFO: remote-Control '\(name)' (v.\(version)) was authorized")
    return .authentication(status: .success)
  }
}
