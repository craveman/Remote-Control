
import NIO
import NIOExtras
import XCTest

@testable import sm02


final class PingBroadcastServiceTests: XCTestCase {

  func testPingBroadcastServiceSendsPingMessage () {
    var expect = expectation(description: "Catch ping message")

    var catchResult: [UInt8]?
    var catcher = PingCatcher({
        catchResult = $0
        expect.fulfill()
    })
    defer {
      catcher.close()
    }

    guard let pingBroadcastService = PingBroadcastService() else {
      XCTFail("Couldn't start ping broadcast service")
      return
    }
    defer {
      pingBroadcastService.stop()
    }
    pingBroadcastService.start()

    waitForExpectations(timeout: 3)
    XCTAssertEqual(catchResult, [0x50, 0x49, 0x4E, 0x47])
  }

  func testPingBroadcastServiceInSM02Server () {
    var expect = expectation(description: "Catch ping message")

    var catchResult: [UInt8]?
    var catcher = PingCatcher({
        catchResult = $0
        expect.fulfill()
    })
    defer {
      catcher.close()
    }

    let server = SM02().start()
    defer {
      server.stop()
    }

    waitForExpectations(timeout: 3)
    XCTAssertEqual(catchResult, [0x50, 0x49, 0x4E, 0x47])
  }

  static var allTests = [
    ("testPingBroadcastServiceSendsPingMessage", testPingBroadcastServiceSendsPingMessage),
    ("testPingBroadcastServiceInSM02Server", testPingBroadcastServiceInSM02Server),
  ]
}


typealias InboundHandler = (_ bytes: [UInt8]) -> Void

class PingCatcher {

  let group: MultiThreadedEventLoopGroup
  let channel: Channel

  init (_ handler: @escaping InboundHandler) {
    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    let bootstrap = DatagramBootstrap(group: group)
        .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 10)
        .channelInitializer { channel in
            channel.pipeline.addHandlers([DebugInboundEventsHandler(), Catcher(handler)])
    }

    channel = try! bootstrap.bind(host: "127.0.0.1", port: 21075).wait()
  }

  func close () {
    let _ = channel.close()
    try! group.syncShutdownGracefully()
  }
}

private class Catcher: ChannelInboundHandler {

  typealias InboundIn = AddressedEnvelope<ByteBuffer>

  let handler: InboundHandler

  init (_ handler: @escaping InboundHandler) {
    self.handler = handler
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let requestEnvelope = self.unwrapInboundIn(data)
    var requestData = requestEnvelope.data
    let bytes = requestData.readBytes(length: requestData.readableBytes)!
    handler(bytes)
  }

  public func channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush()
  }

  public func errorCaught(ctx: ChannelHandlerContext, error: Error) {
    ctx.close(promise: nil)
  }
}
