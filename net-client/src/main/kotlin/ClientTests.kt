import io.netty.buffer.Unpooled
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer

class ClientTests : AbstractVerticle() {
    override fun start(startFuture: Future<Void>) {
        val client = vertx.createNetClient()
        client.connect(9999, "localhost") {res ->
            if (res.succeeded()) {
                startFuture.complete()
                println("Connected!")
                val socket = res.result()
                val buf = Unpooled.buffer()
                buf.writeShort(0x42)
                buf.writeInt(0xF1 and 123)
                buf.writeInt(4)
                buf.writeCharSequence("haha", Charsets.UTF_8)
                socket.write(Buffer.buffer(buf))
                socket.handler {buffer->
                    print("recv $buffer")
                    buf.setInt(3, 0xF2 and 123)
                    socket.write(Buffer.buffer(buf))
                }
            } else {
                startFuture.fail(res.cause())
            }
        }
//        vertx.executeBlocking<Any>({f ->
//            f.complete("haha")
//        }, {res ->
//            println(res)
//        })
//        vertx.executeBlocking<Any>({ f ->
//            // 调用一些需要耗费显著执行时间返回结果的阻塞式API
////            val result = someAPI.blockingMethod("hello")
//            f.complete("")
//        }, false, { res -> println("The result is: " + res.result()) })
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