package com.olrtc.ws.server;

import com.olrtc.ws.config.NettyConfigProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njy
 * @since 2023/2/1 15:17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyServerBoot {
    private final ServerBootstrap       serverBootstrap;
    private final NioEventLoopGroup     boosGroup;
    private final NioEventLoopGroup     workerGroup;
    private final NettyConfigProperties nettyConfigProperties;


    /**
     * tcp连接池
     * newInstance方法如果已存在会抛异常,valueOf不会
     * https://blog.csdn.net/linuu/article/details/51502136
     */
    public static final AttributeKey<Object> NETTY_CHANNEL_KEY = AttributeKey.newInstance("curDeviceSn");
    /**
     * 存储已登录的channel
     * key:deviceSn，value:Channel
     */
    public static final Map<String, Channel> NETTY_CHANNEL_MAP = new ConcurrentHashMap<>(128);


    /**
     * netty start
     *
     * @author njy
     * @since 17:37 2022/12/28
     */
    @PostConstruct
    public void start() throws InterruptedException {
        // 绑定端口启动
        ChannelFuture channelFuture = serverBootstrap.bind(nettyConfigProperties.getPort())
                .syncUninterruptibly();
        if (null != channelFuture && channelFuture.isSuccess()) {
            log.info("===========Netty startup success port:{}===========", nettyConfigProperties.getPort());
        } else {
            log.error("===========Netty startup error!===========");

        }
    }

    /**
     * 优雅关闭
     *
     * @author njy
     * @since 17:37 2022/12/28
     */
    @PreDestroy
    public void close() throws InterruptedException {
        log.info("===========关闭Netty服务器===========");
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}

