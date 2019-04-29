package com.twh.commons.loadbalancer;

/**
 * 负载均衡策略
 * @author tanwenhai@bilibili.com
 */
public interface IRule<T extends INode> {
    /**
     * 选择一个服务节点
     * @param key
     * @return
     */
    T choose(Object key);

    /**
     * 设置负载服务组
     * @param lb
     */
    void setLoadBalancer(ILoadBalancer<T> lb);

    /**
     * 获取负载服务组
     * @return
     */
    ILoadBalancer<T> getLoadBalancer();
}
