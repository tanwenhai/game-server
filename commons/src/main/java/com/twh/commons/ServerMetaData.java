package com.twh.commons;

import com.google.common.base.Charsets;
import lombok.*;

/**
 * @author tanwenhai@bilibili.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
