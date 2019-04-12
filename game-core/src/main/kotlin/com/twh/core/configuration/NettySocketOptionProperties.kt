package com.twh.core.configuration

class NettySocketOptionProperties {
    /**
     * 周期性测试连接是否存活
     */
    internal var keepalive = true

    /**
     * 握手队列大小
     */
    internal var backlog = 1 shl 8

    /**
     * 接收的socket缓冲区
     */
    internal var rcvbuf = 1 shl 12

    /**
     * 写的socket缓冲区
     */
    internal var sndbuf = 1 shl 12

    /**
     * tcp no delay
     */
    internal var nodelay = true

    internal var autoClose = false

    internal var autoRead = true

    internal var reuseaddr = false

    /**
     * 等待client连接的超时时间
     */
    internal var timeout = 6000

    internal var tcpquickack = false
}