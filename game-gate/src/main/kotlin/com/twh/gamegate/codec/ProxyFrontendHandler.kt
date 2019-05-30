package com.twh.gamegate.codec

import com.twh.commons.ServerType
import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import io.netty.channel.*
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
@ChannelHandler.Sharable
class ProxyFrontendHandler(private val lb: ILoadBalancer<INode>) : SimpleChannelInboundHandler<ClientMsg>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val channelMap = ConcurrentHashMap<INode, ProxyBackendHandler>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("客户端建立连接 {}", ctx.channel().remoteAddress())
        ctx.channel().read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ClientMsg) {
        val node = lb.chooseNode(msg.serverType)
        // TODO 没有获取到一个可用的服务
        val streamChannel = channelMap.getOrPut(node) {
            ProxyBackendHandler(node, ctx.channel())
        }
        streamChannel.write(msg)
    }

}