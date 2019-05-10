package com.twh.commons.loadbalancer;

import com.twh.commons.ServerMetaData;
import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * 一致性hash策略
 * @author tanwenhai@bilibili.com
 */
public class ConsistencyHashRule<T extends INode> extends AbstractLoadBalancerRule<T> {

    /**
     * 一致性hash环
     */
    SortedMap<Long, T> ring = new TreeMap<>();

    /**
     * 虚拟节点 解决分布不均匀问题
     */
    private int vnodeCount;

    public ConsistencyHashRule() {
        this(0);
    }

    public ConsistencyHashRule(int vnodeCount) {
        this.vnodeCount = vnodeCount;
    }

    @Override
    public T choose0(ILoadBalancer<T> lb, Object key) {
        return null;
    }

    @Override
    public void setLoadBalancer(ILoadBalancer<T> lb) {
        RingLoadBalancer newLb = new RingLoadBalancer(lb);
        super.setLoadBalancer(newLb);
    }

    private class RingLoadBalancer implements ILoadBalancer<T> {
        ILoadBalancer<T> lb;
        RingLoadBalancer(ILoadBalancer<T> lb) {
            this.lb = lb;
        }

        @Override
        public void addServers(List<T> newServers) {
            lb.addServers(newServers);
            // 添加服务放入一致性hash环
            for (T newServer : newServers) {
                int exists = getReplicas(newServer.name());
                for (int i = 0; i < vnodeCount; i++) {
                    @SuppressWarnings("unchecked")
                    T vNode = (T)new VirtualNode(newServer, exists + i);
                    ring.put(hash(vNode.name()), vNode);
                }
            }
        }

        @Override
        public T chooseNode(Object key) {
            SortedMap<Long, T> ring = ConsistencyHashRule.this.ring;
            if (ring.isEmpty()) {
                return null;
            }
            long hashKey = hash(key.toString());
            SortedMap<Long, T> tailMap = ring.tailMap(hashKey);

            // 顺时针最近的一个节点
            hashKey = !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
            return (T)((VirtualNode)ring.get(hashKey)).getParent();
        }

        @Override
        public List<T> getAllNodes() {
            return lb.getAllNodes();
        }

        @Override
        public void markServerDown(T node) {
            lb.markServerDown(node);
            // 从一致性hash环移出相关虚拟节点
            Iterator<Long> it = ring.keySet().iterator();
            while (it.hasNext()) {
                Long key = it.next();
                T vNode = ring.get(key);
                if (vNode.name().startsWith(node.name())) {
                    it.remove();
                }
            }
        }

        long hash(String name) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("不支持m5d");
            }

            // ..............
            md5.reset();
            md5.update(name.getBytes(Charset.forName("UTF-8")));
            byte[] digest = md5.digest();
            long h = 0L;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }

        int getReplicas(String nodeName) {
            int replicas = 0;
            for (T node : ring.values()) {
                if (node.name().startsWith(nodeName)) {
                    replicas++;
                }
            }
            return replicas;
        }
    }

    private class VirtualNode implements INode {

        INode parent;

        int subIndex;

        VirtualNode(INode parent, int subIndex) {
            this.parent = parent;
        }

        @Override
        public boolean isAlive() {
            return parent.isAlive();
        }

        @Override
        public String name() {
            return parent.name() + "-" + subIndex;
        }

        @Override
        public Channel newChannel(Function<ServerMetaData, Channel> function) {
            return parent.newChannel(function);
        }

        INode getParent() {
            return parent;
        }
    }
}
