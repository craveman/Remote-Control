//
//  Sm02UdpLookup.swift
//
//  Created by Sergei Andreev on 16.08.2020.
//

import NIO

private final class EchoHandler: ChannelHandler, ChannelInboundHandler {
  typealias InboundIn = AddressedEnvelope<ByteBuffer>
  
  public func channelRead(ctx: ChannelHandlerContext, data: NIOAny) {
    let addressedEnvelope = self.unwrapInboundIn(data)
    print("Recieved data from \(addressedEnvelope.remoteAddress)")
    ctx.write(data, promise: nil)
  }
  
  public func channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush()
  }
  
  public func errorCaught(ctx: ChannelHandlerContext, error: Error) {
    print("error :", error)
    
    ctx.close(promise: nil)
  }
}


private final class LogHandler: ChannelHandler, ChannelInboundHandler {
  typealias InboundIn = AddressedEnvelope<ByteBuffer>
  
  public func handlerAdded(ctx: ChannelHandlerContext) {
    
  }
  
  public func handlerRemoved(ctx: ChannelHandlerContext) {
    
  }
  
  public func channelRead(ctx: ChannelHandlerContext, data: NIOAny) {
    let addressedEnvelope = self.unwrapInboundIn(data)
    print("LogHandler Recieved data from \(addressedEnvelope.remoteAddress)")
    print(data)
  }
  
  public func channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush()
  }
  
  public func errorCaught(ctx: ChannelHandlerContext, error: Error) {
    print("error :", error)
    
    ctx.close(promise: nil)
  }
}


private final class SearchSmOptionHandler: ChannelHandler, ChannelInboundHandler {
  typealias InboundIn = AddressedEnvelope<ByteBuffer>
  
  public func channelRead(ctx: ChannelHandlerContext, data: NIOAny) {
    let addressedEnvelope = self.unwrapInboundIn(data)
    print("SearchHandler Recieved data from \(addressedEnvelope.remoteAddress)")
    let option = RemoteAddress(
      ssid: "",
      ip: "\(addressedEnvelope.remoteAddress)",
      code: [0,0,0,0,0]
    )
    Sm02.lanConnectionOptions = [option]
  }
  
  public func channelReadComplete(ctx: ChannelHandlerContext) {
    ctx.flush()
  }
  
  public func errorCaught(ctx: ChannelHandlerContext, error: Error) {
    print("error :", error)
    
    ctx.close(promise: nil)
  }
}

public let defaultUdpListenPort = 21075

public class Sm02UdpLookup {
  private let group: MultiThreadedEventLoopGroup
  private let bootstrap: DatagramBootstrap
  private var channel: Channel?
  
  public init() {
    self.group = MultiThreadedEventLoopGroup(numberOfThreads: System.coreCount)
    self.bootstrap = DatagramBootstrap(group: group)
      .channelOption(ChannelOptions.socket(SocketOptionLevel(SOL_SOCKET), SO_REUSEADDR), value: 1)
      .channelInitializer({ (ch: Channel) -> EventLoopFuture<Void> in
        ch.pipeline.addHandler(LogHandler())
//        ch.pipeline.addHandler(SearchSmOptionHandler())
      })
    
    //        .channelInitializer { channel in
    //          channel.pipeline.addHandlers([LogHandler, SearchSmOptionHandler])
    defer {
      try! group.syncShutdownGracefully()
    }
  }
  
  public func stop() -> Void {
    try! self.channel?.close().wait()
  }
  
  public func start(_ port: Int = defaultUdpListenPort) throws {
    
    self.channel = try! bootstrap.bind(host: "0.0.0.0", port: port).wait()
    
    guard let channel = self.channel else {
      print("Failed to start channel")
      return
    }
    
    print("Channel accepting connections on \(String(describing: channel.localAddress))")
    
    try! channel.closeFuture.wait()
    
    print("Channel closed")
  }
}
