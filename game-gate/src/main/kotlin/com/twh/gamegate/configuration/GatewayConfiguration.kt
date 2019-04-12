package com.twh.gamegate.configuration

import com.twh.commons.NettyServerProperties
import com.twh.commons.NettySocketOptionProperties
import io.netty.channel.EventLoopGroup
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContextException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "netty.server.socket")
    fun nettySocketOptionProperties() = NettySocketOptionProperties()

    @Bean
    @ConfigurationProperties(prefix = "netty.server")
    fun nettyServerProperties() = NettyServerProperties()

    @Bean(destroyMethod = "shutdownGracefully")
    fun bossGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().bossGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("bossGroup参数配置错误", e)
        }

    }

    @Bean(destroyMethod = "shutdownGracefully")
    fun workGroup(): EventLoopGroup {
        try {
            return nettyServerProperties().workGroup.newInstance()
        } catch (e: Exception) {
            throw ApplicationContextException("workGroup参数配置错误", e)
        }

    }

//    @Bean(destroyMethod = "shutdownGracefully")
//    fun blockGroup(): EventLoopGroup {
//        try {
//            return nettyServerProperties().blockGroup.newInstance()
//        } catch (e: Exception) {
//            throw ApplicationContextException("workGroup参数配置错误", e)
//        }
//    }
}