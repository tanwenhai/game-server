package com.twh.gamegate.codec

import com.twh.commons.ServerMetaData
import com.twh.commons.ServerStatus
import com.twh.commons.ServerType
import com.twh.commons.loadbalancer.BaseLoadBalancer
import com.twh.commons.loadbalancer.INode
import com.twh.commons.loadbalancer.RoundRobinRule
import com.twh.commons.loadbalancer.ServerNode
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class ProxyFrontendHandlerTest {

    @Test
    fun channelRead0() {
        val lb = BaseLoadBalancer<INode>(RoundRobinRule<INode>())
        val node = ServerNode(ServerMetaData(ServerType.MATCH, ServerStatus.NORMAL, "127.0.0.1", 9000))
        lb.addServers(listOf(node))
        var channel = EmbeddedChannel(
                AuthHandler(),
                ClientMsgDecoder(),
                ProxyFrontendHandler(lb)
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
        channel.readOutbound<ByteBuf>()
    }
}