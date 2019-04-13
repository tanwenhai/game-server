package com.twh.core

import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.util.concurrent.DefaultThreadFactory
import java.util.concurrent.ThreadFactory

class EventLoopGroupProperties(private val threadPrefix: String) {
    /**
     * @see .newInstance
     */
    private val eventLoopGroup = NioEventLoopGroup::class.java

    /**
     * 线程数
     */
    private val threads = 0

    @Throws(Exception::class)
    fun newInstance(): EventLoopGroup {
        val threadFactory = DefaultThreadFactory(threadPrefix)

        val constructor = eventLoopGroup.getConstructor(Int::class.javaPrimitiveType, ThreadFactory::class.java)
        return constructor.newInstance(threads, threadFactory)
    }
}