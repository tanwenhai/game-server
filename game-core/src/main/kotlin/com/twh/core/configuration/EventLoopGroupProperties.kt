package com.twh.core.configuration

import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.util.concurrent.DefaultThreadFactory
import java.util.concurrent.ThreadFactory

open class EventLoopGroupProperties {
    /**
     * @see .newInstance
     */
    var eventLoopGroup:Class<out EventLoopGroup> = NioEventLoopGroup::class.java

    /**
     * 线程数
     */
    var threads = 0

    var threadPrefix = "io"

    @Throws(Exception::class)
    fun newInstance(): EventLoopGroup {
        val threadFactory = DefaultThreadFactory(threadPrefix)

        val constructor = eventLoopGroup.getConstructor(Int::class.javaPrimitiveType, ThreadFactory::class.java)
        return constructor.newInstance(threads, threadFactory)
    }
}