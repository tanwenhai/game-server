package com.twh.commons;

/**
 * @author tanwenhai@bilibili.com
 */
public enum ServerType {
    /**
     * 房间服
     */
    ROOM((short)1) {
        @Override
        public boolean test(int cmd) {
            return (cmd & 0xF1) == cmd;
        }
    },
    MATCH((short)2) {
        @Override
        public boolean test(int cmd) {
            return (cmd & 0xF2) == cmd;
        }
    },
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

    public abstract boolean test(int cmd);

    public short getValue() {
        return value;
    }
}
