import com.twh.commons.ServerType
import io.netty.buffer.Unpooled
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.core.net.netClientOptionsOf

class ClientTests : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        val options = netClientOptionsOf(
                connectTimeout = 10000)
        val client = vertx.createNetClient(options)
        val future = Future.future<NetSocket> {res ->
            if (res.succeeded()) {
                startFuture.complete()
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
                startFuture.fail(res.cause())
            }
        }
        client.connect(9999, "localhost", future)
        vertx.executeBlocking<Any>({f ->
            f.complete("haha")
        }, {res ->
            println(res)
        })
        vertx.executeBlocking<Any>({ f ->
            // 调用一些需要耗费显著执行时间返回结果的阻塞式API
//            val result = someAPI.blockingMethod("hello")
            f.complete("")
        }, false, { res -> println("The result is: " + res.result()) })
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ClientTests::class.java.name) {res ->
        if (!res.succeeded()) {
            vertx.close()
        }
    }
}