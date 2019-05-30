package com.twh.gamegate.bootstrap

import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import com.twh.core.configuration.NettyServerProperties
import com.twh.gamegate.codec.AuthHandler
import com.twh.gamegate.codec.ClientMsgDecoder
import com.twh.gamegate.codec.ProxyFrontendHandler
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
    lateinit var lb: ILoadBalancer<INode>

    override fun initChannel(ch: SocketChannel) {
        if (serverProperties.ssl) {
            val ssc = SelfSignedCertificate()
            val sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build()
            // tls layer
            ch.pipeline().addFirst(sslCtx.newHandler(ch.alloc()))
        }
        ch.pipeline()
                .addLast(AuthHandler())
                .addLast(ClientMsgDecoder())
                .addLast(ProxyFrontendHandler(lb))
    }
}

