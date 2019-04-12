package com.twh.commons;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * @author tanwenhai@bilibili.com
 */
@Slf4j
@Builder
public class GameServer<C extends Channel> {
    private final NettySocketOptionProperties nettySocketOptionProperties;

    private final NettyServerProperties serverProperties;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workGroup;

    private final ChannelInitializer<C> connectionInitializer;

    public GameServer(NettySocketOptionProperties nettySocketOptionProperties, NettyServerProperties serverProperties,
                      EventLoopGroup bossGroup, EventLoopGroup workGroup, ChannelInitializer<C> connectionInitializer) {
        this.nettySocketOptionProperties = nettySocketOptionProperties;
        this.serverProperties = serverProperties;
        this.bossGroup = bossGroup;
        this.workGroup = workGroup;
        this.connectionInitializer = connectionInitializer;
    }

    public void start() throws InterruptedException {
        val b = new ServerBootstrap();
        // 设置处理连接的线程池和读写的线程池
        b.group(bossGroup, workGroup).channel(serverProperties.getChannel());

        // 设置tcp三次握手的队列长度
        b.option(ChannelOption.SO_BACKLOG, nettySocketOptionProperties.getBacklog());
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        if (log.isDebugEnabled()) {
            b.handler(new LoggingHandler(LogLevel.DEBUG));
        }

        // 连接处理
        b.childHandler(connectionInitializer);

        b.childOption(ChannelOption.SO_KEEPALIVE, nettySocketOptionProperties.getKeepalive());
        b.childOption(ChannelOption.AUTO_CLOSE, nettySocketOptionProperties.getAutoClose());
        b.childOption(ChannelOption.AUTO_READ, nettySocketOptionProperties.getAutoRead());
        b.childOption(ChannelOption.SO_REUSEADDR, nettySocketOptionProperties.getReuseaddr());
        b.childOption(ChannelOption.SO_RCVBUF, nettySocketOptionProperties.getRcvbuf());
        b.childOption(ChannelOption.SO_SNDBUF, nettySocketOptionProperties.getSndbuf());
        b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        // 如果是linux环境使用EpollServerSocketChannel
        if (Epoll.isAvailable() && serverProperties.getChannel() == EpollServerSocketChannel.class) {
            b.childOption(EpollChannelOption.TCP_QUICKACK, true);
        }
        val f = b.bind(serverProperties.getAddress(), serverProperties.getPort()).sync();
        log.info("started and listening for connections on" + f.channel().localAddress());
        f.channel().closeFuture().sync();
    }
}
