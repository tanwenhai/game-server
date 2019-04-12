package com.twh.gamegate.bootstrap

import com.twh.core.ServerType
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import org.slf4j.LoggerFactory

/**
 * 消息转发
 */
class ProxyFrontendHandler : ChannelInboundHandlerAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val serverChannelMap = HashMap<ServerType, ChannelFuture>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("client establish connection {}", ctx.channel().remoteAddress())
        ctx.channel().read()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        log.debug("receive client msg")
        if (msg is ByteBuf) {
            val readableBytes = msg.readableBytes()
            // 可读取的字节数大于6，serverType占两个字节len四个字节data最少需要一个字节
            if (readableBytes > 6) {
                msg.markReaderIndex()
                val serverType: ServerType? = try {
                    ServerType.from(msg.readShort())
                } catch (ignore: Exception) {
                    null
                }

                if (serverType !== null) {
                    val len = msg.readInt()
                    if (readableBytes - 6 == len) {
                        // 有一个完整的消息需要转发
                        val forwardMsg = msg.readBytes(readableBytes - 6)
                        // 根据serverType获取一个连接并把消息转发过去

                        backendChannel(serverType, ctx.channel()).addListener{ conn ->
                            if (conn is ChannelFuture && conn.isSuccess) {
                                conn.channel().writeAndFlush(forwardMsg).addListener { future ->
                                    if (future.isSuccess) {
                                        // 转发完了，继续读取下一个要转发的消息
                                        log.debug("forward to backend done next read client msg")
                                        ctx.read()
                                    } else {
                                        // TODO 失败了
                                        log.warn("forward to backend({}) fail", conn.channel().remoteAddress())
                                    }
                                }
                            }
                        }

                        return
                    }
                }
                // 不需要转发，重置
                msg.resetReaderIndex()
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }

    private fun backendChannel(serverType: ServerType, inboundChannel: Channel): ChannelFuture {
        return serverChannelMap.getOrPut(serverType) {
            val b = Bootstrap()
            b.group(inboundChannel.eventLoop())
                    .channel(inboundChannel::class.java)
                    .handler(ProxyBackendHandler(inboundChannel))
                    .option(ChannelOption.AUTO_READ, false)

            // 建立到后端的服务器
            b.connect("127.0.0.1", 8888)
        }
    }
}