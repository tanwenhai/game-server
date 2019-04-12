package com.twh.core.configuration

import io.netty.channel.EventLoopGroup
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContextException
import org.springframework.context.annotation.Bean

class GameServerAutoConfiguration {

    @ConditionalOnMissingBean(NettySocketOptionProperties::class)
    @Bean
    @ConfigurationProperties(prefix = "netty.server.socket")
    fun nettySocketOptionProperties() = NettySocketOptionProperties()

    @ConditionalOnMissingBean
    @Bean
    @ConfigurationProperties(prefix = "netty.server")
    fun nettyServerProperties() = NettyServerProperties()

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdownGracefully")
    fun bossGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().bossGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("bossGroup参数配置错误", e)
        }

    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdownGracefully")
    fun workGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().workGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("workGroup参数配置错误", e)
        }
    }

    @ConditionalOnMissingBean
    @Bean
    fun zookeeperOption() = ZookeeperOption()

    @ConditionalOnMissingBean
    @Bean
    fun gameServerBootstrap() = GameServerBootstrap()
}