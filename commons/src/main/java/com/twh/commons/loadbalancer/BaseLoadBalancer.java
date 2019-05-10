package com.twh.commons.loadbalancer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author tanwenhai@bilibili.com
 */
public class BaseLoadBalancer<T extends INode> implements ILoadBalancer<T> {
    IRule<T> rule;

    CopyOnWriteArrayList<T> servers;

    public BaseLoadBalancer(IRule<T> rule) {
        this.rule = rule;
    }

    @Override
    public void addServers(List<T> newServers) {
        servers.addAll(newServers);
    }

    @Override
    public T chooseNode(Object key) {
        return rule.choose(key);
    }

    @Override
    public List<T> getAllNodes() {
        return servers.subList(0, servers.size());
    }

    @Override
    public void markServerDown(T node) {

    }
}
