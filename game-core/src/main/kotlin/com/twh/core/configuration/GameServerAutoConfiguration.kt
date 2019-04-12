package com.twh.core.configuration

import io.netty.channel.EventLoopGroup
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContextException
import org.springframework.context.annotation.Bean

class GameServerAutoConfiguration {

    @ConditionalOnMissingBean(NettySocketOptionProperties::class)
    @Bean
    fun nettySocketOptionProperties() = NettySocketOptionProperties()

    @ConditionalOnMissingBean(NettyServerProperties::class)
    @Bean
    fun nettyServerProperties() = NettyServerProperties()

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["bossGroup"])
    @Bean(name = ["bossGroup"], destroyMethod = "shutdownGracefully")
    fun bossGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().bossGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("bossGroup参数配置错误", e)
        }

    }

    @ConditionalOnMissingBean(EventLoopGroup::class, name = ["workGroup"])
    @Bean(name = ["workGroup"], destroyMethod = "shutdownGracefully")
    fun workGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().workGroup.newInstance()
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
}