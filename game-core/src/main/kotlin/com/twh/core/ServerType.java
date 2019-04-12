package com.twh.core;

/**
 * @author tanwenhai@bilibili.com
 */
public enum ServerType {
    /**
     * 房间服
     */
    ROOM((short)1);

    final short value;

    ServerType(short i) {
        this.value = i;
    }

    public static ServerType from(short i) {
        for (ServerType value : values()) {
            if (value.value == i) {
                return value;
            }
        }

        throw new IllegalArgumentException("illegal argument " + i);
    }

    public short getValue() {
        return value;
    }
}
