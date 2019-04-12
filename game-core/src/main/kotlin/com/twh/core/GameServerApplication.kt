package com.twh.core

import com.twh.commons.*
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContextException

open class GameServerApplication : ApplicationRunner {
    @Autowired
    lateinit var nettySocketOptionProperties: NettySocketOptionProperties

    @Autowired
    lateinit var serverProperties: NettyServerProperties

    @Autowired
    lateinit var bossGroup: EventLoopGroup

    @Autowired
    lateinit var workGroup: EventLoopGroup

    @Autowired
    lateinit var connectionInitializer: ChannelInitializer<SocketChannel>

    override fun run(args: ApplicationArguments?) {
        val zkCli = ZooKeeper("zoo1:2181", 5000) {}
        val data = ServerMetaData.builder()
                .serverType(ServerType.ROOM)
                .ip("127.0.0.1")
                .port(8888)
                .serverStatus(ServerStatus.NORMAL)
                .build()
                .toJsonByteArray()
        zkCli.create("/game-server/${serverProperties.name}", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
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