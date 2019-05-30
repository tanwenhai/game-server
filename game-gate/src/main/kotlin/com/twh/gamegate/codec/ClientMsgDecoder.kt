package com.twh.gamegate.codec

import com.twh.commons.ServerType
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.LoggerFactory

class ClientMsgDecoder : ByteToMessageDecoder() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
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
                break
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
            out.add(ClientMsg(cmd, len, msg.readBytes(len)))
            return
        }
        msg.resetReaderIndex()
    }

}