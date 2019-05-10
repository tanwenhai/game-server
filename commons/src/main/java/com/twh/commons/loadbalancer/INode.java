package com.twh.commons.loadbalancer;

import com.twh.commons.ServerMetaData;
import io.netty.channel.Channel;

import java.util.function.Function;

/**
 * @author tanwenhai@bilibili.com
 */
public interface INode {
    /**
     * 是否可用
     * @return
     */
    boolean isAlive();

    /**
     * 节点名
     * @return
     */
    String name();

    /**
     * 建立一个连接到node的channel
     * @param function
     * @return
     */
    Channel newChannel(Function<ServerMetaData, Channel> function);
}
