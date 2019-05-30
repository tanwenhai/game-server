package com.twh.gamegate.bootstrap

import com.twh.commons.ServerType
import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import com.twh.gamegate.codec.ClientMsg
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
        log.debug("收到客户端消息")

        val cmd = msg.cmd
        var serverType: ServerType? = null
        for (value in ServerType.values()) {
            if (value.test(cmd)) {
                serverType = value
                break
            }
        }

        if (serverType == null) {
            // 收到一个错误的消息
            log.debug("收到一个错误消息 cmd={} 没有目标服务", cmd)
            ctx.close()
            return
        }
        forward2server(serverType, ctx, msg)
    }

    private fun forward2server(serverType: ServerType, ctx: ChannelHandlerContext, msg: ClientMsg) {
        val node = lb.chooseNode(serverType)
        val streamChannel = channelMap.getOrPut(node) {
            ProxyBackendHandler(node, ctx.channel())
        }
        streamChannel.write(msg)
    }
}