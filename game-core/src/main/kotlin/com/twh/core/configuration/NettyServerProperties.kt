package com.twh.core.configuration

import com.twh.core.EventLoopGroupProperties
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.InetAddress

@ConfigurationProperties(prefix = "netty.server")
class NettyServerProperties {
    internal var name: String = "default"

    /**
     * 服务器端口
    */
    internal var port = 9999

    /**
     * 服务器IP
     */
    internal var address = InetAddress.getLoopbackAddress()

    /**
     * 开启 SSL
     */
    internal var ssl = false

    /**
     * 处理业务的线程组
     */
    internal var blockGroup = EventLoopGroupProperties("business")

    /**
     * 处理连接的线程组
     */
    internal var bossGroup = EventLoopGroupProperties("boss")

    /**
     * 处理读写的线程组
     */
    internal var workGroup = EventLoopGroupProperties("work")

    /**
     * channel 类型 windows nio/linux epool
     */
    internal var channel = NioServerSocketChannel::class.java
}