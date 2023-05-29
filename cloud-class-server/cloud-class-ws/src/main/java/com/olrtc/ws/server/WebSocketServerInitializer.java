package com.olrtc.ws.server;

import com.olrtc.ws.handler.RequestHandler;
import com.olrtc.ws.handler.WebSocketServerHandler;
import com.olrtc.ws.remote.RemoteUserService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * @author njy
 * @since 2023/2/1 15:13
 */
@Component
@RequiredArgsConstructor
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final RemoteUserService remoteUserService;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        WebSocketServerProtocolConfig config = WebSocketServerProtocolConfig.newBuilder()
                .websocketPath("/websocket")
                .allowExtensions(true)
                .handleCloseFrames(false)
                .checkStartsWith(true)
                .handshakeTimeoutMillis(10)
//                .dropPongFrames(false)
                .build();

        ChannelPipeline pipeline = ch.pipeline();
        //IdleStateHandler心跳机制 https://blog.csdn.net/u013967175/article/details/78591810
        // pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 0, 0, TimeUnit.MINUTES));
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new RequestHandler(remoteUserService)); // 鉴权
        pipeline.addLast(new WebSocketServerHandler()) // ws消息处理
        ;


    }
}