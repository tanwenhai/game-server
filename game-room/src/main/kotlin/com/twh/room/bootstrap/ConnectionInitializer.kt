package com.twh.room.bootstrap

import com.twh.commons.NettyServerProperties
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ConnectionInitializer : ChannelInitializer<SocketChannel>() {
    @Autowired
    lateinit var serverProperties: NettyServerProperties

    @Autowired
    lateinit var echoHandler: EchoHandler

    override fun initChannel(ch: SocketChannel) {
        if (serverProperties.isSsl) {
            val ssc = SelfSignedCertificate()
            val sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build()
            // tls layer
            ch.pipeline().addFirst(sslCtx.newHandler(ch.alloc()))
        }
        ch.pipeline().addLast(echoHandler)
    }
}