package com.twh.commons.zookeeper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanwenhai@bilibili.com
 */
public class ZKConnection {
    private final ZooKeeper zkCli;

    public ZKConnection(String connectString) throws IOException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.zkCli = new ZooKeeper(connectString, 5000, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }


}
