package com.twh.core

import com.twh.commons.ServerMetaData
import com.twh.commons.ServerStatus
import com.twh.commons.ServerType
import com.twh.core.configuration.ZookeeperOption
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.net.InetAddress

/**
 * todo 服务状态维护
 */
@Component
class ServerListener : ApplicationRunner {
    @Autowired
    lateinit var zookeeperOption: ZookeeperOption

    @Value("\${netty.server.name}")
    var serverName: String? = null

    @Value("\${netty.server.type}")
    var serverType: ServerType? = null

    @Value("\${netty.server.address}")
    var serverAddress: InetAddress? = null

    @Value("\${netty.server.port}")
    var serverPort: Int? = null

    override fun run(args: ApplicationArguments) {
        if (serverType !== null && serverName !== null && serverAddress !== null && serverPort !== null) {
            val zkCli = ZooKeeper(zookeeperOption.connection, 5000) {}
            // 服务启动向zk注册
            val serverMetaData = ServerMetaData.builder()
                    .serverType(serverType)
                    .serverStatus(ServerStatus.NORMAL)
                    .ip(serverAddress!!.hostAddress)
                    .port(serverPort!!)
                    .build()
            zkCli.create("${zookeeperOption.rootPath}/$serverName",
                    serverMetaData.toJsonByteArray(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
        }


    }

}