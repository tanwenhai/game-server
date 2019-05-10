package com.twh.gamegate

import com.twh.commons.loadbalancer.ILoadBalancer
import com.twh.commons.loadbalancer.INode
import com.twh.core.GameServer
import com.twh.core.configuration.NettyServerProperties
import com.twh.core.configuration.NettySocketOptionProperties
import com.twh.core.configuration.ZookeeperOption
import com.twh.gamegate.bootstrap.ConnectionInitializer
import com.twh.gamegate.bootstrap.ServerListener
import io.netty.channel.EventLoopGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContextException

@SpringBootApplication
class GameGateApplication: ApplicationRunner {

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

    @Autowired
    lateinit var lb: ILoadBalancer<INode>

    @Throws
    override fun run(args: ApplicationArguments) {
        ServerListener(zookeeperOption, lb).updateServerList()
        try {
            GameServer(nettySocketOptionProperties, serverProperties, bossGroup, workGroup, connectionInitializer).start()
        } catch (e: Exception) {
            throw ApplicationContextException(e.message!!, e)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GameGateApplication>(*args)
}
