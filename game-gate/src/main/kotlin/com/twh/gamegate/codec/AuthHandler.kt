package com.twh.gamegate.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.ByteProcessor

/**
 * 用户验证
 */
@ChannelHandler.Sharable
class AuthHandler : SimpleChannelInboundHandler<ByteBuf>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        var i = msg.forEachByte(ByteProcessor.FIND_LF)
        val userName = msg.readCharSequence(i, Charsets.UTF_8)
        msg.readByte()
        i = msg.forEachByte(ByteProcessor.FIND_LF) - i - 1
        val passWord = msg.readCharSequence(i, Charsets.UTF_8)

        if (userName == "twh" && passWord == "123") {
            ctx.pipeline().remove(this)
            ctx.writeAndFlush("登录成功")
        } else {
            ctx.writeAndFlush("验证不通过")
        }
    }
}