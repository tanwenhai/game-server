package com.twh.core.configuration

import com.twh.commons.ServerType
import com.twh.core.EventLoopGroupProperties
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.InetAddress

@ConfigurationProperties(prefix = "netty.server")
class NettyServerProperties {

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
    var blockGroup = EventLoopGroupProperties("business")

    /**
     * 处理连接的线程组
     */
    var bossGroup = EventLoopGroupProperties("boss")

    /**
     * 处理读写的线程组
     */
    var workGroup = EventLoopGroupProperties("work")

    /**
     * channel 类型 windows nio/linux epool
     */
    var channel = NioServerSocketChannel::class.java
}