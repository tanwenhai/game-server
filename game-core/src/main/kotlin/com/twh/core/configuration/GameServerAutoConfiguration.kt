package com.twh.core.configuration

import com.twh.core.ServerListener
import io.netty.channel.EventLoopGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContextException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
    fun serverListener() = ServerListener()
}