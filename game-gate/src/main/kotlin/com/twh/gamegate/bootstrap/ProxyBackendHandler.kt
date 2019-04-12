package com.twh.gamegate.bootstrap

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.LoggerFactory

/**
 * 将后端返回的数据转发的客户端
 */
class ProxyBackendHandler(private val inboundChannel: Channel) : ChannelInboundHandlerAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("connection to backend server {}", ctx.channel().remoteAddress())
        ctx.read()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        log.debug("receive backend msg")
        // 后端返回的数据转发给客户端
        inboundChannel.writeAndFlush(msg).addListener {future ->
            if (future.isSuccess) {
                // 继续读后端返回的数据
                log.debug("next read backend server msg")
                ctx.read()
            } else {
                // TODO 失败了
                log.warn("write msg to client({}) fail", inboundChannel.remoteAddress())
            }
        }
    }
}