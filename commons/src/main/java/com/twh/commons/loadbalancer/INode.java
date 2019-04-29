package com.twh.commons.loadbalancer;

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
}
