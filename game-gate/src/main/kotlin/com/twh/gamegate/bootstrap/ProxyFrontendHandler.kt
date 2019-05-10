package com.twh.gamegate.bootstrap

import com.twh.commons.ServerType
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import org.slf4j.LoggerFactory

/**
 * 消息转发
 * <pre>
 * +-----+-----+-----+
 * | magic:2 | cmd:4 | len:4 | data |
 * +-----+-----+-----+
 * </pre>
 */
class ProxyFrontendHandler : SimpleChannelInboundHandler<ByteBuf>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val serverChannelMap = HashMap<ServerType, ChannelFuture>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("客户端建立连接 {}", ctx.channel().remoteAddress())
        ctx.channel().read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        log.debug("收到客户端消息")

        msg.markReaderIndex()
        val magic = msg.readShort();
        if (magic != 0x42.toShort()) {
            // 收到一个错误的消息
            log.debug("收到一个错误消息 magic={}", magic)
            ctx.close()
            return
        }
        val cmd = msg.readInt()
        var serverType: ServerType? = null
        for (value in ServerType.values()) {
            if (value.test(cmd)) {
                serverType = value
            }
        }

        if (serverType == null) {
            // 收到一个错误的消息
            log.debug("收到一个错误消息 cmd={} 没有目标服务", cmd)
            ctx.close()
            return
        }

        val len = msg.readInt()
        if (msg.readableBytes() >= len) {
            // 至少有一个完整的消息需要转发
            val forwardMsg = msg.readBytes(msg.readableBytes())
            forward2server(serverType, ctx, forwardMsg)
            return
        }
        msg.resetReaderIndex()
    }

    private fun forward2server(serverType: ServerType, ctx: ChannelHandlerContext, msg: ByteBuf) {
        // 根据serverType获取一个连接并把消息转发过去
        backendChannel(serverType, ctx.channel()).addListener{ conn ->
            if (conn is ChannelFuture && conn.isSuccess) {
                conn.channel().writeAndFlush(msg).addListener { future ->
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