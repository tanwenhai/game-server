package com.twh.gamegate.codec

import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.Test

import org.junit.Assert.*

class ClientMsgDecoderTest {

    @Test
    fun decode() {
        var channel = EmbeddedChannel(
                AuthHandler(),
                ClientMsgDecoder()
        )
        channel.writeInbound(Unpooled.copiedBuffer("twh\n123\n".toByteArray()))
        var msg = channel.readOutbound<String>()
        assertEquals(msg, "登录成功")
        assertNull(channel.pipeline().get(AuthHandler::class.java))

        val send = Unpooled.buffer()
        send.writeShort(0x42)
        send.writeInt(123444 and 0xF1)
        send.writeInt(4)
        send.writeBytes("haha".toByteArray())
        channel.writeInbound(send)
    }
}