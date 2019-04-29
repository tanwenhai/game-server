package com.twh.commons.loadbalancer;

/**
 * @author tanwenhai@bilibili.com
 */
public abstract class AbstractLoadBalancerRule<T extends INode> implements IRule<T> {
    private ILoadBalancer<T> lb;

    @Override
    public void setLoadBalancer(ILoadBalancer<T> lb) {
        this.lb = lb;
    }

    @Override
    public T choose(Object key) {
        return choose0(getLoadBalancer(), key);
    }

    public abstract T choose0(ILoadBalancer<T> lb, Object key);

    @Override
    public ILoadBalancer<T> getLoadBalancer() {
        return lb;
    }
}
