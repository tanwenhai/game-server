package com.twh.core.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class StreamMsgCodec : ByteToMessageCodec<StreamMsg>() {
    override fun encode(ctx: ChannelHandlerContext, msg: StreamMsg, out: ByteBuf) {
        out.writeInt(msg.len)
        out.writeInt(msg.streamId)
        out.writeBytes(msg.payload)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val len = msg.readInt()
        val streamId = msg.readInt()
        if (msg.readableBytes() >= len) {
            val payload = msg.readBytes(len)
            out.add(StreamMsg(len, streamId, payload))
        }
    }
}