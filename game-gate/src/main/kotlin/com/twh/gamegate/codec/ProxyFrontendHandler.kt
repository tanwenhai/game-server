package com.twh.gamegate.codec

import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import io.netty.channel.*
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.util.AttributeKey
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 消息转发
 * <pre>
 * +-----+-----+-----+-----+-----+-----+
 * | magic:2 | cmd:4 |len:4|    data   |
 * +-----+-----+-----+-----+-----+-----+
 * </pre>
 */
val STREAM_ID_KEY: AttributeKey<Int> = AttributeKey.newInstance("streamId")

@ChannelHandler.Sharable
class ProxyFrontendHandler(private val lb: ILoadBalancer<INode>) : SimpleChannelInboundHandler<ClientMsg>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val channelMap = ConcurrentHashMap<INode, ProxyBackendHandler>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("客户端建立连接 {}", ctx.channel().remoteAddress())
        ctx.channel().attr(STREAM_ID_KEY).set(999)
        ctx.channel().read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ClientMsg) {
        val node = lb.chooseNode(msg.serverType)
        // TODO 没有获取到一个可用的服务
        val bch = channelMap.getOrPut(node) {
            ProxyBackendHandler(node, ctx.channel())
        }
        if (bch.channel().eventLoop().inEventLoop()) {
            bch.write(msg)
        } else {
            val future = bch.channel().eventLoop().submit {
                bch.write(msg)
            }
            if (ctx.channel()::class.java === EmbeddedChannel::class.java) {
                future.await()
            }
        }
    }

}