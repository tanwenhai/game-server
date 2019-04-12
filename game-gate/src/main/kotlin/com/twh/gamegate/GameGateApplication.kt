package com.twh.gamegate

import com.twh.commons.GameServer
import com.twh.commons.NettyServerProperties
import com.twh.commons.NettySocketOptionProperties
import com.twh.gamegate.bootstrap.ConnectionInitializer
import com.twh.gamegate.bootstrap.ServerListener
import com.twh.gamegate.configuration.ZookeeperOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextException

@SpringBootApplication
class GameGateApplication: ApplicationRunner {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var zookeeperOption: ZookeeperOption

    @Autowired
    lateinit var nettySocketOptionProperties: NettySocketOptionProperties

    @Autowired
    lateinit var serverProperties: NettyServerProperties

    @Autowired
    lateinit var bossGroup: EventLoopGroup

    @Autowired
    lateinit var workGroup: EventLoopGroup

    @Autowired
    lateinit var connectionInitializer: ConnectionInitializer

    override fun run(args: ApplicationArguments) {
        ServerListener(zookeeperOption).updateServerList()
        try {
            GameServer.builder<SocketChannel>()
                    .nettySocketOptionProperties(nettySocketOptionProperties)
                    .serverProperties(serverProperties)
                    .bossGroup(bossGroup)
                    .workGroup(workGroup)
                    .connectionInitializer(connectionInitializer)
                    .build()
                    .start()
        } catch (e: Exception) {
            throw ApplicationContextException(e.message!!, e)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GameGateApplication>(*args)
}
