package com.twh.core.configuration

import com.twh.core.ServerStatWatcher
import io.netty.buffer.ByteBuf
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
import java.net.Socket

@Configuration
@EnableConfigurationProperties(NettyServerProperties::class, NettySocketOptionProperties::class, ZookeeperOption::class)
class GameServerAutoConfiguration {

    @Autowired
    lateinit var nettyServerProperties: NettyServerProperties

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["bossGroup"])
    @Bean(name = ["bossGroup"], destroyMethod = "shutdownGracefully")
    fun bossGroup(): EventLoopGroup {
        try {
            return nettyServerProperties.bossGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("bossGroup参数配置错误", e)
        }

    }

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["workGroup"])
    @Bean(name = ["workGroup"], destroyMethod = "shutdownGracefully")
    fun workGroup(): EventLoopGroup {
        try {
            return nettyServerProperties.workGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("workGroup参数配置错误", e)
        }
    }

    @ConditionalOnMissingBean(ZookeeperOption::class)
    @Bean
    fun zookeeperOption() = ZookeeperOption()

    @ConditionalOnMissingBean(GameServerBootstrap::class)
    @Bean
    fun gameServerBootstrap() = GameServerBootstrap()

    @Bean
    fun serverListener() = ServerStatWatcher()

    /**
     * 默认丢弃所有数据
     */
    @ConditionalOnMissingBean(ChannelInitializer::class)
    @Bean
    fun connectionInitializer(): ChannelInitializer<SocketChannel> {
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
