package com.twh.gamegate.codec

import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.Assert.*
import org.junit.Test

class AuthHandlerTest {
    @Test
    fun channelRead0() {
        var channel = EmbeddedChannel(
                AuthHandler()
        )
        channel.writeInbound(Unpooled.copiedBuffer("twh\n123\n".toByteArray()))
        var msg = channel.readOutbound<String>()
        assertEquals(msg, "登录成功")
        assertNull(channel.pipeline().get(AuthHandler::class.java))

        channel.pipeline().addLast(AuthHandler())
        channel.writeInbound(Unpooled.copiedBuffer("twh1\n123\n".toByteArray()))
        msg = channel.readOutbound<String>()
        assertEquals(msg, "验证不通过")
    }
}