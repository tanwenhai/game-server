package com.twh.core

import com.twh.core.configuration.NettyServerProperties
import com.twh.core.configuration.NettySocketOptionProperties
import com.twh.core.configuration.ZookeeperOption
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollChannelOption
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import org.slf4j.LoggerFactory

class GameServer<C : Channel>(private val zookeeperOption: ZookeeperOption,
                              private val nettySocketOptionProperties: NettySocketOptionProperties,
                              private val serverProperties: NettyServerProperties,
                              private val bossGroup: EventLoopGroup,
                              private val workGroup: EventLoopGroup,
                              private val connectionInitializer: ChannelInitializer<C>) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Throws(InterruptedException::class)
    fun start() {
        val b = ServerBootstrap()
        // 设置处理连接的线程池和读写的线程池
        b.group(bossGroup, workGroup).channel(serverProperties.channel)

        // 设置tcp三次握手的队列长度
        b.option(ChannelOption.SO_BACKLOG, nettySocketOptionProperties.backlog)
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)

        if (log.isDebugEnabled) {
            b.handler(LoggingHandler(LogLevel.DEBUG))
        }

        // 连接处理
        b.childHandler(connectionInitializer)

        b.childOption(ChannelOption.SO_KEEPALIVE, nettySocketOptionProperties.keepalive)
        b.childOption(ChannelOption.AUTO_CLOSE, nettySocketOptionProperties.autoClose)
        b.childOption(ChannelOption.AUTO_READ, nettySocketOptionProperties.autoRead)
        b.childOption(ChannelOption.SO_REUSEADDR, nettySocketOptionProperties.reuseaddr)
        b.childOption(ChannelOption.SO_RCVBUF, nettySocketOptionProperties.rcvbuf)
        b.childOption(ChannelOption.SO_SNDBUF, nettySocketOptionProperties.sndbuf)
        b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator(true))
        // 接收bytebuf
        b.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator())
        // 如果是linux环境使用EpollServerSocketChannel
        if (Epoll.isAvailable() && serverProperties.channel === EpollServerSocketChannel::class.java) {
            b.childOption(EpollChannelOption.TCP_QUICKACK, true)
        }
        val f = b.bind(serverProperties.address, serverProperties.port).sync()
        log.info("started and listening for connections on" + f.channel().localAddress())
        f.addListener {
            if (it.isSuccess) {
                // 註冊
                ServerStatWatcher().watch(zookeeperOption, serverProperties)
            }
        }
        f.channel().closeFuture().sync()
    }
}