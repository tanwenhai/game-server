package com.twh.commons.loadbalancer;

import java.util.List;

/**
 * @author tanwenhai@bilibili.com
 */
public class BaseLoadBalancer<T extends INode> implements ILoadBalancer<T> {
    IRule<T> rule;

    @Override
    public void addServers(List<T> newServers) {

    }

    @Override
    public T chooseNode(Object key) {
        return rule.choose(key);
    }

    @Override
    public List<T> getAllNodes() {
        return null;
    }

    @Override
    public void markServerDown(T node) {

    }
}
