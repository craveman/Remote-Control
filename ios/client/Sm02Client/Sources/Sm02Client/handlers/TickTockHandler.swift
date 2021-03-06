
import NIO
import Foundation


final class TickTockHandler: ChannelInboundHandler {

  typealias Container = Singletons
  typealias InboundIn = ByteBuffer
  typealias OutboundOut = ByteBuffer
  typealias TockReceivedAction = () -> Void

  private static let TICK_TAG: UInt8 = 0xF1
  private static let TICK_REQUEST_STATUS: UInt8 = 0x00
  private static let TOCK_TAG: UInt8 = 0xF2
  private static let TOCK_RESPONSE_STATUS: UInt8 = 0x01

  var tockReceivedAction: TockReceivedAction? = nil

  private let events: HandlersManager<ConnectionEvent>

  private var tickMessage: ByteBuffer? = nil
  private var channel: Channel? = nil
  private var scheduledJobId: UUID? = nil

  init (container: Container) {
    events = container.eventsManager
  }

  public func channelActive (context: ChannelHandlerContext) {
    channel = context.channel

    if tickMessage == nil {
      tickMessage = context.channel.allocator.buffer(capacity: 2)
      tickMessage!.writeInteger(TickTockHandler.TICK_TAG as UInt8)
      tickMessage!.writeInteger(TickTockHandler.TICK_REQUEST_STATUS as UInt8)
    }

    scheduledJobId = Scheduler.shared.schedule(every: .seconds(2), run: { [weak self] in
        guard let self = self else {
          print("WARN: the handler is not available for sending TICK message")
          return
        }
        guard var tick = self.tickMessage else {
          print("WARN: TICK bytes buffer is not available for sending TICK message")
          return
        }
        guard let channel = self.channel else {
          print("WARN: channel is not available for sending TICK message")
          return
        }

        tick.moveReaderIndex(to: 0)
        let out = self.wrapOutboundOut(tick)
        channel.writeAndFlush(out, promise: nil)
    })
  }

  public func channelInactive (context: ChannelHandlerContext) {
    guard let jobId = scheduledJobId else {
      return
    }
    Scheduler.shared.cancel(id: jobId)
  }

  public func channelRead (context: ChannelHandlerContext, data: NIOAny) {
    let buffer = unwrapInboundIn(data)

    guard let tag = buffer.getInteger(at: 0, as: UInt8.self) else {
      let message = "message doesn't contain a tag"
      let error = ConnectionError.parsingdError(message)
      context.fireErrorCaught(error)
      return
    }

    if tag != TickTockHandler.TOCK_TAG {
      context.fireChannelRead(data)
      return
    }

    guard let status = buffer.getInteger(at: 1, as: UInt8.self) else {
      let message = "a tock message doesn't contain a status"
      let error = ConnectionError.parsingdError(message)
      context.fireErrorCaught(error)
      return
    }

    if status == TickTockHandler.TOCK_RESPONSE_STATUS {
      tockReceivedAction?()
      return
    }

    let message = "tock (tag: \(tag)) message has invalid status value - \(status)"
    let error = ConnectionError.parsingdError(message)
    context.fireErrorCaught(error)
  }
}
