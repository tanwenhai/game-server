package com.twh.core.codec

import io.netty.buffer.ByteBuf

data class StreamMsg (
    val len: Int,
    val streamId: Int,
    val payload: ByteBuf
)