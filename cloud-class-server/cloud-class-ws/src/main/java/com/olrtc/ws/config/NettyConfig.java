package com.olrtc.ws.config;

import com.olrtc.ws.server.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author njy
 * @since 2023/2/1 15:10
 */
@Configuration
@RequiredArgsConstructor
public class NettyConfig {

    private final NettyConfigProperties nettyConfigProperties;
    private final WebSocketServerInitializer webSocketServerInitializer;

    /**
     * boss 线程池
     * 负责客户端连接
     */
    @Bean
    public NioEventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyConfigProperties.getBoss());
    }

    /**
     * worker线程池
     * 负责业务处理
     */
    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyConfigProperties.getWorker());
    }

    /**
     * 服务器启动器
     */
    @Bean
    public ServerBootstrap serverBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup(), workerGroup())                 // 指定使用的线程组
                .channel(NioServerSocketChannel.class)                    // 指定使用的通道
                .option(ChannelOption.SO_BACKLOG, 1024)             // socket参数，当服务器请求处理线程全满时，用于临时存放已完成三次握手请求的队列的最大长度
                .childOption(ChannelOption.TCP_NODELAY, true)       // 数据来了，立即发送
                .childOption(ChannelOption.SO_KEEPALIVE, true)      // 启用心跳保活机制，tcp，默认2小时发一次心跳
                .childHandler(webSocketServerInitializer);                       // 指定worker处理器
        return serverBootstrap;
    }
}
