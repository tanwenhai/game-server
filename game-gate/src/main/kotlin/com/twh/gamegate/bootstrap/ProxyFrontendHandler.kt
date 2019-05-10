package com.twh.gamegate.bootstrap

import com.twh.commons.ServerType
import com.twh.commons.loadbalancer.BaseLoadBalancer
import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import org.slf4j.LoggerFactory

/**
 * 消息转发
 * <pre>
 * +-----+-----+-----+-----+-----+-----+
 * | magic:2 | cmd:4 |len:4|    data   |
 * +-----+-----+-----+-----+-----+-----+
 * </pre>
 */
class ProxyFrontendHandler(private val lb: ILoadBalancer<INode>) : SimpleChannelInboundHandler<ByteBuf>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val serverChannelMap = HashMap<ServerType, INode>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.debug("客户端建立连接 {}", ctx.channel().remoteAddress())
        ctx.channel().read()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        log.debug("收到客户端消息")

        msg.markReaderIndex()
        val magic = msg.readShort();
        if (magic != 0x42.toShort()) {
            // 收到一个错误的消息
            log.debug("收到一个错误消息 magic={}", magic)
            ctx.close()
            return
        }
        val cmd = msg.readInt()
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

        val len = msg.readInt()
        if (msg.readableBytes() >= len) {
            // 至少有一个完整的消息需要转发
            val forwardMsg = msg.readBytes(msg.readableBytes())
            forward2server(serverType, ctx, forwardMsg)
            return
        }
        msg.resetReaderIndex()
    }

    private fun forward2server(serverType: ServerType, ctx: ChannelHandlerContext, msg: ByteBuf) {
        if (!serverChannelMap.contains(serverType)) {
            val node = lb.chooseNode(serverType)
            if (node == null) {
                // TODO 根据serverType没有找到可用的后端服务 发送一个响应告知客户端
                ctx.read()
                return
            }

        }

        val listener = ChannelFutureListener {
            if (it.isSuccess) {
                it.channel().writeAndFlush(msg.copy()).addListener {
                    future ->
                    if (future.isSuccess) {
                        // 转发完了，继续读取下一个要转发的消息
                        log.debug("转发至后端服务完成，继续读取消息")
                        ctx.read()
                    } else {
                        // 失败了， 将失败节点移除重试下一个节点
                        log.error("转发至后端服务失败", future.cause())
                        lb.markServerDown(node)
                        forward2server(serverType, ctx, msg)
                    }
                }
            }
        }

        node.newChannel { metaData ->
            val b = Bootstrap()
            b.group(ctx.channel().eventLoop())
                    .channel(ctx.channel()::class.java)
                    .handler(ProxyBackendHandler(ctx.channel()))
                    .option(ChannelOption.AUTO_READ, false)

            // 建立到后端的服务器
            b.connect(metaData.ip, metaData.port).addListener (listener).channel()
        }

        val serverChannel = node.channel {metaData ->
            val b = Bootstrap()
            b.group(ctx.channel().eventLoop())
                    .channel(ctx.channel()::class.java)
                    .handler(ProxyBackendHandler(ctx.channel()))
                    .option(ChannelOption.AUTO_READ, false)

            // 建立到后端的服务器
            b.connect(metaData.ip, metaData.port).addListener (listener).channel()
        }

        if (serverChannel.isOpen && serverChannel.isActive) {
            serverChannel.newSucceededFuture().addListener(listener)
        }
    }

    private fun selectNode(serverType: ServerType): INode? {
        return serverChannelMap.getOrPut(serverType, {
            val node = lb.chooseNode(serverType)
            if (node == null) {
                lb.markServerDown(node)
            }
            node
        })
    }
}