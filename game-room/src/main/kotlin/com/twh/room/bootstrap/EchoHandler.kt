package com.twh.room.bootstrap

import com.twh.core.codec.StreamMsg
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 协议转发
 */
@ChannelHandler.Sharable
@Component
class EchoHandler : SimpleChannelInboundHandler<StreamMsg>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("connection {}", ctx.channel().remoteAddress())
        super.channelActive(ctx)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: StreamMsg) {
        log.debug("receive msg")
        ctx.writeAndFlush(msg)
    }
}