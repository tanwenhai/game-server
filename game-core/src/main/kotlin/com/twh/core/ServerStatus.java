package com.twh.core;

/**
 * @author tanwenhai@bilibili.com
 */
public enum ServerStatus {
    /**
     * 正常
     */
    NORMAL((short)0),

    /**
     * 准备关闭，不提供服务，处理完请求之后可以关闭
     */
    PREPARE_CLOSE((short)1),
    ;

    private final short value;

    ServerStatus(short i) {
        value = i;
    }

    public short getValue() {
        return value;
    }
}
