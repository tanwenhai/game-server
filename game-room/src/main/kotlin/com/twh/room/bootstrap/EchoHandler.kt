package com.twh.room.bootstrap

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.springframework.stereotype.Component

/**
 * 协议转发
 */
@ChannelHandler.Sharable
@Component
class EchoHandler : SimpleChannelInboundHandler<ByteBuf>() {

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        ctx.writeAndFlush(msg.readBytes(msg.readableBytes()))
    }
}