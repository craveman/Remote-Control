
import NIO
import NIOExtras
import XCTest

import logging
import sm02

@testable import networking


final class ReadTimeoutTests: XCTestCase {

  static var allTests = [
    ("testReadTimeoutFailure", testReadTimeoutFailure),
    ("testsReadTimeoutSuccess", testsReadTimeoutSuccess),
  ]

  func testReadTimeoutFailure () {
    LogContext.ROOT.logLevel = .DEBUG

    let server = SilentServer()

    sleep(1)

    let client = TcpClient()
    client.connect(to: try! SocketAddress(ipAddress: "127.0.0.1", port: 21078))

    XCTAssertTrue(client.channel!.isActive)

    sleep(7)

    XCTAssertFalse(client.channel!.isActive)
  }

  func testsReadTimeoutSuccess () {
    LogContext.ROOT.logLevel = .DEBUG

    let server = SM02().start()

    sleep(1)

    let client = TcpClient()
    client.connect(to: try! SocketAddress(ipAddress: "127.0.0.1", port: 21074))

    XCTAssertTrue(client.channel!.isActive)

    sleep(7)

    XCTAssertTrue(client.channel!.isActive)
  }
}

class SilentServer {

  let group: MultiThreadedEventLoopGroup
  let channel: Channel

  init () {
    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    let bootstrap = ServerBootstrap(group: group)
        .serverChannelOption(ChannelOptions.backlog, value: 64)
        .serverChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .childChannelOption(ChannelOptions.socket(IPPROTO_TCP, TCP_NODELAY), value: 1)
        .childChannelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .childChannelOption(ChannelOptions.maxMessagesPerRead, value: 1)
        .childChannelOption(ChannelOptions.recvAllocator, value: AdaptiveRecvByteBufferAllocator())
        .childChannelInitializer { channel in
            channel.pipeline.addHandlers([SilentHandler()])
    }

    channel = try! bootstrap.bind(host: "127.0.0.1", port: 21078).wait()
  }

  deinit {
    close()
  }

  func close () {
    let _ = channel.close()
    try! group.syncShutdownGracefully()
  }
}

class SilentHandler: ChannelInboundHandler {

  typealias InboundIn = ByteBuffer

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
  }
}
