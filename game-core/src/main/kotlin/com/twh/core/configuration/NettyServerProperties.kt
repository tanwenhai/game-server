package com.twh.core.configuration

import com.twh.commons.ServerType
import io.netty.channel.ServerChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.net.InetAddress

@ConfigurationProperties(prefix = "netty.server")
open class NettyServerProperties {

    var name: String = "default"

    var serverType: ServerType? = null

    /**
     * 服务器端口
    */
    var port = 9999

    /**
     * 服务器IP
     */
    var address: InetAddress = InetAddress.getLoopbackAddress()

    /**
     * 开启 SSL
     */
    var ssl = false

    /**
     * 处理业务的线程组
     */
    @NestedConfigurationProperty
    var blockGroup = EventLoopGroupProperties()

    /**
     * 处理连接的线程组
     */
    @NestedConfigurationProperty
    var bossGroup = EventLoopGroupProperties()

    /**
     * 处理读写的线程组
     */
    @NestedConfigurationProperty
    var workGroup = EventLoopGroupProperties()

    /**
     * channel 类型 windows nio/linux epool
     */
    var channel: Class<out ServerChannel> = NioServerSocketChannel::class.java
}