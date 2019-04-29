package com.twh.commons.loadbalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机策略
 * @author tanwenhai@bilibili.com
 */
public class RandomRule<T extends INode> extends AbstractLoadBalancerRule<T> {

    @Override
    public T choose0(ILoadBalancer<T> lb, Object key) {
        if (lb == null) {
            return null;
        }

        T node = null;
        while (node == null) {
            List<T> allNodes = lb.getAllNodes();
            int nodeCount = allNodes.size();
            if (nodeCount == 0) {
                return null;
            }

            int index = randomInt(nodeCount);
            node = allNodes.get(index);
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

    private int randomInt(int nodeCount) {
        return ThreadLocalRandom.current().nextInt(nodeCount);
    }
}
