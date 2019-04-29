package com.twh.commons.loadbalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询策略
 * @author tanwenhai@bilibili.com
 */
public class RoundRobinRule<T extends INode> extends AbstractLoadBalancerRule<T> {
    private AtomicInteger nextServerCyclicCounter;

    public RoundRobinRule() {
        nextServerCyclicCounter = new AtomicInteger(0);

    }

    @Override
    public T choose0(ILoadBalancer<T> lb, Object key) {
        if (lb == null) {
            return null;
        }

        T node = null;
        int count = 0;
        while (node == null && count++ < 10) {
            List<T> allNodes = lb.getAllNodes();
            int nodeCount = allNodes.size();

            if (nodeCount == 0) {
                return null;
            }

            int nextNodeIndex = incrementAndGetModulo(nodeCount);
            node = allNodes.get(nextNodeIndex);

            if (node == null) {
                // 让出cpu稍后重试
                Thread.yield();
                continue;
            }

            // 节点是否可用
            if (node.isAlive()) {
                return node;
            }
            node = null;
        }

        return node;
    }

    /**
     * cas操作获取下标
     * @param modulo
     * @return
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;

            if (nextServerCyclicCounter.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}
