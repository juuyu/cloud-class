package com.olrtc.ws.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author njy
 * @since 2023/2/1 15:10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "olrtc.netty")
public class NettyConfigProperties {
    /**
     * 服务器主端口 默认7000
     */
    private Integer port = 7001;
    /**
     * 服务器备用端口 默认70001
     */
    private Integer portSalve = 7002;
    /**
     * 服务器地址 默认为本地
     */
    private String host = "127.0.0.1";
    /**
     * boss线程数量,默认为cpu核心数*2
     */
    private int boss = 1;
    /**
     * worker线程数量,默认为cpu核心数*2
     */
    private int worker;
    /**
     * 连接超时时间 默认为30s
     */
    private Integer timeout = 30000;
}

