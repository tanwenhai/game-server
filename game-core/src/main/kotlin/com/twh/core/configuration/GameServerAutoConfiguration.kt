package com.twh.core.configuration

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.SocketChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContextException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(NettyServerProperties::class, NettySocketOptionProperties::class, ZookeeperOption::class)
open class GameServerAutoConfiguration {

    @Autowired
    lateinit var nettyServerProperties: NettyServerProperties

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["bossGroup"])
    @Bean(name = ["bossGroup"], destroyMethod = "shutdownGracefully")
    open fun bossGroup(): EventLoopGroup {
        try {
            return nettyServerProperties.bossGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("bossGroup参数配置错误", e)
        }

    }

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["workGroup"])
    @Bean(name = ["workGroup"], destroyMethod = "shutdownGracefully")
    open fun workGroup(): EventLoopGroup {
        try {
            return nettyServerProperties.workGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("workGroup参数配置错误", e)
        }
    }

    @ConditionalOnMissingBean(ZookeeperOption::class)
    @Bean
    open fun zookeeperOption() = ZookeeperOption()

    @ConditionalOnMissingBean(GameServerBootstrap::class)
    @Bean
    open fun gameServerBootstrap() = GameServerBootstrap()

//    @Bean
//    open fun serverListener() = ServerStatWatcher()

    /**
     * 默认丢弃所有数据
     */
    @ConditionalOnMissingBean(ChannelInitializer::class)
    @Bean
    open fun connectionInitializer(): ChannelInitializer<SocketChannel> {
        return object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline().addLast(object: SimpleChannelInboundHandler<Any>() {
                    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any) {
                    }

                    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                        ctx.close()
                    }
                })
            }
        }
    }
}
