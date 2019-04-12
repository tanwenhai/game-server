package com.twh.core;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * zookeeper 服务注册
 * @author tanwenhai@bilibili.com
 */
public class ServerRegister {

    private final ZookeeperConfig config;

    private final ZooKeeper zk;

    public ServerRegister(ZookeeperConfig config) throws IOException {
        this.config = config;
        zk = new ZooKeeper("localhost:2181", 2000, event -> {
        });
    }

    public void register(ServerMetaData metaData) throws KeeperException, InterruptedException, UnknownHostException {
        // 创建一个瞬时节点
        byte[] bytes = metaData.toJsonByteArray();
        zk.create("/game-server/" + metaData.getServerType().name(), bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
}
