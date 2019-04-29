package com.twh.commons.loadbalancer;

import java.util.List;

/**
 * 负载均衡
 * @author tanwenhai@bilibili.com
 */
public interface ILoadBalancer<T extends INode> {
    /**
     * 添加服务节点
     * @param newServers
     */
    void addServers(List<T> newServers);

    /**
     * 从负载均衡组选择一个节点
     * @param key
     * @return
     */
    T chooseNode(Object key);

    /**
     * 获取所有节点
     * @return
     */
    List<T> getAllNodes();

    /**
     * 移除一个节点
     * @param node
     */
    void markServerDown(T node);
}
