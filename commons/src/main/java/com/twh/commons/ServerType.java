package com.twh.commons;

/**
 * @author tanwenhai@bilibili.com
 */
public enum ServerType {
    /**
     * 房间服
     */
    ROOM((short)1),
    MATCH((short)2),
    ;

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
