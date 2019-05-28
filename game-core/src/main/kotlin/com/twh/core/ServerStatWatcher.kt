package com.twh.core

import com.twh.commons.ServerMetaData
import com.twh.commons.ServerStatus
import com.twh.core.configuration.NettyServerProperties
import com.twh.core.configuration.ZookeeperOption
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

/**
 * todo 服务状态维护
 */
class ServerStatWatcher {
    fun watch(zookeeperOption: ZookeeperOption, serverProperties: NettyServerProperties) {
        val serverName = serverProperties.name

        if (serverProperties.serverType !== null && serverProperties.name.isNotEmpty()) {
            val zkCli = ZooKeeper(zookeeperOption.connection, 5000) {}
            // 服务启动向zk注册
            val serverMetaData = ServerMetaData.builder()
                    .serverType(serverProperties.serverType)
                    .serverStatus(ServerStatus.NORMAL)
                    .ip(serverProperties.address.hostAddress)
                    .port(serverProperties.port)
                    .build()
            zkCli.create("${zookeeperOption.rootPath}/$serverName",
                    serverMetaData.toJsonByteArray(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
        }
    }

}