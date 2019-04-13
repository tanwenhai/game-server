package com.twh.gamegate.bootstrap

import com.twh.core.configuration.ZookeeperOption
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

/**
 * 服务器监听
 */
class ServerListener(private val option: ZookeeperOption) {
    private val zkCli: ZooKeeper = ZooKeeper(option.connection, 5000) {}

    /**
     * 更新服务器列表
     */
    fun updateServerList() {
        val rootPath = option.rootPath
        if (zkCli.exists(rootPath, false) == null) {
            zkCli.create(rootPath, "".toByteArray(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        }

        loopWatchChildren(rootPath, Watcher {
            if (it.path !== null) {
                when(it.type) {
                    Watcher.Event.EventType.NodeChildrenChanged -> println("NodeChildrenChanged")
                    Watcher.Event.EventType.NodeCreated -> println("NodeCreated")
                    Watcher.Event.EventType.NodeDataChanged -> println("NodeDataChanged")
                    Watcher.Event.EventType.NodeDeleted -> println("NodeDeleted")
                    else -> print("it.type")
                }
            }
        })
    }

    private fun loopWatchChildren(rootPath: String, wc: Watcher) {
        val nodes = zkCli.getChildren(rootPath) {
            wc.process(it)
            loopWatchChildren(rootPath, wc)
        }

        // 获取节点数据
        for (node in nodes) {
            val bytes = zkCli.getData("$rootPath/$node", false, null)
            println(String(bytes))
        }
    }
}