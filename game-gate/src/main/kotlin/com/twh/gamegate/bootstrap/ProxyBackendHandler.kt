package com.twh.gamegate.bootstrap

import com.twh.commons.loadbalancer.INode
import com.twh.gamegate.codec.ClientMsg
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelOption
import org.slf4j.LoggerFactory

/**
 * 将后端返回的数据转发的客户端
 * @param inboundChannel 客户端的连接
 */
class ProxyBackendHandler(private val node: INode, private val inboundChannel: Channel)  : ChannelInboundHandlerAdapter() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val backendChannel: Channel

    init {
        backendChannel = node.newChannel {
            val b = Bootstrap()
            b.group(inboundChannel.eventLoop())
                    .channel(inboundChannel::class.java)
                    .handler(this)
                    .option(ChannelOption.AUTO_READ, false)

            // 建立连接
            b.connect(it.ip, it.port).sync().channel()
        }
    }

    fun write(msg: ClientMsg) {
        // TODO 添加区分客户端连接的标记之后发给后端服务
        backendChannel.writeAndFlush("").addListener {
            if (it.isSuccess) {
                // 转发完了，继续读取下一个要转发的消息
                inboundChannel.read()
            } else {
                // TODO 失败了
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("connection to backend server {}", ctx.channel().remoteAddress())
        ctx.read()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        log.debug("receive backend msg")
        // TODO 根据标记后端返回的数据转发给客户端
        inboundChannel.writeAndFlush(msg).addListener {future ->
            if (future.isSuccess) {
                // 继续读后端返回的数据
                log.debug("next read backend server msg")
                ctx.read()
            } else {
                // TODO 失败了
                log.warn("write msg to client({}) fail", inboundChannel.remoteAddress())
            }
        }
    }
}