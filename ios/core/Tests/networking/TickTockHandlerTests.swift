
import NIO
import NIOExtras
import XCTest

import logging
import sm02

@testable import networking


final class TickTockHandlerTests: XCTestCase {

  static var allTests = [
    ("testSendTickAndReceiveTock", testSendTickAndReceiveTock),
  ]

  func testSendTickAndReceiveTock () {
    LogContext.ROOT.logLevel = .DEBUG

    var expect = expectation(description: "Catch tock message")
    expect.expectedFulfillmentCount = 3

    let server = SM02().start()
    defer {
      server.stop()
    }

    sleep(1)

    let client = MyClient(tockReceivedAction: {
      expect.fulfill()
    })
    defer {
      client.close()
    }

    waitForExpectations(timeout: 15)
  }
}

final class MyClient {

  let group: MultiThreadedEventLoopGroup
  let bootstrap: ClientBootstrap
  let channel: Channel

  init (tockReceivedAction: @escaping TickTockHandler.TockReceivedAction) {
    let sharedTickTockHandler = TickTockHandler()
    sharedTickTockHandler.tockReceivedAction = tockReceivedAction

    let sharedLogOnErrorHandler = LogOnErrorHandler()
    let sharedCloseOnErrorHandler = NIOCloseOnErrorHandler()

    group = MultiThreadedEventLoopGroup(numberOfThreads: 1)
    bootstrap = ClientBootstrap(group: group)
        .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
        .channelInitializer { channel in
            channel.pipeline.addHandler(BackPressureHandler()).flatMap {
              channel.pipeline.addHandlers([
                  ByteToMessageHandler(LengthFieldBasedFrameDecoder(lengthFieldLength: .two), maximumBufferSize: Int(UInt16.max)),
                  LengthFieldPrepender(lengthFieldLength: .two),
                  sharedTickTockHandler,
                  sharedLogOnErrorHandler,
                  sharedCloseOnErrorHandler,
              ])
          }
        }

    channel = try! bootstrap.connect(host: "127.0.0.1", port: 21074).wait()
  }

  deinit {
    close()
  }

  func close () {
    if !channel.isActive {
      return
    }
    let _ = channel.close()
    try! group.syncShutdownGracefully()
  }
}
