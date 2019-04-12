package com.twh.commons;

import com.google.common.base.Charsets;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author tanwenhai@bilibili.com
 */
@Getter
@Setter
@Builder
public class ServerMetaData {
    private ServerType serverType;

    private ServerStatus serverStatus;

    private String ip;

    private int port;

    public byte[] toJsonByteArray() {
        return JsonUtils.obj2str(this).getBytes(Charsets.UTF_8);
    }

    public ServerMetaData from(byte[] bytes) {
        return JsonUtils.json2pojo(new String(bytes, Charsets.UTF_8), ServerMetaData.class);
    }
}
