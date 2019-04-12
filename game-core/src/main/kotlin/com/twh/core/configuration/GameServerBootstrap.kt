package com.twh.core.configuration

import com.twh.commons.ServerMetaData
import com.twh.commons.ServerStatus
import com.twh.commons.ServerType
import com.twh.core.GameServer
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContextException

open class GameServerBootstrap: ApplicationRunner {
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

    @Autowired
    lateinit var zookeeperOption: ZookeeperOption

    override fun run(args: ApplicationArguments?) {
        val zkCli = ZooKeeper(zookeeperOption.connection, 5000) {}
        val data = ServerMetaData.builder()
                .serverType(ServerType.ROOM)
                .ip("127.0.0.1")
                .port(8888)
                .serverStatus(ServerStatus.NORMAL)
                .build()
                .toJsonByteArray()
        zkCli.create("${zookeeperOption.rootPath}/${serverProperties.name}", data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
        try {
            GameServer(
                nettySocketOptionProperties,
                serverProperties,
                bossGroup,
                workGroup,
                connectionInitializer
            ).start()
        } catch (e: Exception) {
            throw ApplicationContextException(e.message!!, e)
        }
    }
}