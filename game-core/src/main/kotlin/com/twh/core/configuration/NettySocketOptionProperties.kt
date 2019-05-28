package com.twh.core.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "netty.server.socket")
open class NettySocketOptionProperties {
    /**
     * 周期性测试连接是否存活
     */
    var keepalive = true

    /**
     * 握手队列大小
     */
    var backlog = 1 shl 8

    /**
     * 接收的socket缓冲区
     */
    var rcvbuf = 1 shl 12

    /**
     * 写的socket缓冲区
     */
    var sndbuf = 1 shl 12

    /**
     * tcp no delay
     */
    var nodelay = true

    var autoClose = false

    var autoRead = true

    var reuseaddr = false

    /**
     * 等待client连接的超时时间
     */
    var timeout = 6000

    var tcpquickack = false
}