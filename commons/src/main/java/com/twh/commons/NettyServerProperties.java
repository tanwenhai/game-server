package com.twh.commons;

import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;

/**
 * @author tanwenhai@bilibili.com
 */
@Getter
@Setter
public class NettyServerProperties {

    private String name;

    /**
     * 服务器端口
     */
    private Integer port = 9999;

    /**
     * 服务器IP
     */
    private InetAddress address = InetAddress.getLoopbackAddress();

    /**
     * 开启 SSL
     */
    private boolean ssl = false;

    /**
     * 处理业务的线程组
     */
    private EventLoopGroupProperties blockGroup = new EventLoopGroupProperties("business");

    /**
     * 处理连接的线程组
     */
    private EventLoopGroupProperties bossGroup = new EventLoopGroupProperties("boss");

    /**
     * 处理读写的线程组
     */
    private EventLoopGroupProperties workGroup = new EventLoopGroupProperties("work");

    /**
     * channel 类型 windows nio/linux epool
     */
    private Class<? extends ServerChannel> channel = NioServerSocketChannel.class;
}
