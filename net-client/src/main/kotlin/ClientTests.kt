import com.twh.commons.ServerType
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.kotlin.core.net.netClientOptionsOf
import java.util.concurrent.TimeUnit

class ClientTests : AbstractVerticle() {
    override fun start(startFuture: Future<Void>?) {
        val options = netClientOptionsOf(
                connectTimeout = 10000)
        val client = vertx.createNetClient(options)
        client.connect(4321, "localhost") { res ->
            if (res.succeeded()) {
                println("Connected!")
                val socket = res.result()
                socket.handler {buffer->
                    print("recv $buffer")
                    val buf = Unpooled.buffer()
                    buf.writeShort(ServerType.ROOM.value.toInt())
                    buf.writeInt(4)
                    buf.writeCharSequence("haha", Charsets.UTF_8)
                    socket.write(Buffer.buffer(buf))
                }
            } else {
                println("Failed to connect: ${res.cause().message}")
            }
        }
    }
}

fun main() {
    val b = Bootstrap()
    val nioEventLoopGroup = NioEventLoopGroup(1)
    b.group(nioEventLoopGroup)
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInboundHandlerAdapter() {
                override fun channelActive(ctx: ChannelHandlerContext) {

                }

                override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                    ctx.executor().schedule({
                        val buf = Unpooled.buffer()
                        buf.writeShort(ServerType.ROOM.value.toInt())
                        buf.writeInt(4)
                        buf.writeCharSequence("haha", Charsets.UTF_8)
                        ctx.writeAndFlush(buf)
                    }, 1, TimeUnit.MILLISECONDS)
                }
            })
    val f = b.connect("127.0.0.1", 9999)
    f.addListener { future ->
        if (future.isSuccess) {
            println("connection success")
        }
    }
    f.channel().closeFuture().sync()
    nioEventLoopGroup.shutdownGracefully()
}