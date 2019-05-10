package com.twh.commons.loadbalancer;

import com.twh.commons.ServerMetaData;
import io.netty.channel.Channel;

import java.util.function.Function;

/**
 * @author tanwenhai@bilibili.com
 */
public class ServerNode implements INode {
    private final ServerMetaData metaData;

    private Channel channel;

    public ServerNode(ServerMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public boolean isAlive() {
        return channel != null && channel.isActive();
    }

    @Override
    public String name() {
        return metaData.getServerType().name();
    }

    @Override
    public Channel channel(Function<ServerMetaData, Channel> function) {
        if (channel == null) {
            synchronized (this) {
                channel = function.apply(metaData);
            }
        }

        return channel;
    }
}
