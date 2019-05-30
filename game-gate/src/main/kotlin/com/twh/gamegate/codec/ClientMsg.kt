package com.twh.gamegate.codec

import io.netty.buffer.ByteBuf

/**
 * 客户端消息格式
 * +-----+-----+-----+-----+-----+-----+
 * | magic:2 | cmd:4 |len:4|    data   |
 * +-----+-----+-----+-----+-----+-----+
 */
data class ClientMsg(
    val cmd: Int,
    val len: Int,
    val data: ByteBuf
)