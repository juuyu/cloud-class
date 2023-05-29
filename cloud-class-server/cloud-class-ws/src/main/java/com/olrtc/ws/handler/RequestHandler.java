package com.olrtc.ws.handler;

import com.olrtc.common.core.domain.R;
import com.olrtc.ws.cache.ClientCtxUtil;
import com.olrtc.ws.remote.RemoteUserService;
import com.olrtc.ws.remote.model.dto.UserDto;
import com.olrtc.ws.util.NettyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;


/**
 * ws 鉴权校验
 *
 * @author njy
 * @since 2023/2/24 09:48
 */
@Slf4j
public class RequestHandler extends ChannelInboundHandlerAdapter {

    private final RemoteUserService remoteUserService;

    private static final String Sec_Protocol = "Sec-WebSocket-Protocol";

    public RequestHandler(RemoteUserService remoteUserService) {
        this.remoteUserService = remoteUserService;
    }


    @Override
    public void channelRead(@Nonnull ChannelHandlerContext ctx, @Nonnull Object msg) throws Exception {
        if (msg instanceof FullHttpRequest req) {
            HttpHeaders headers = req.headers();
            Channel channel = ctx.channel();

            // 过滤非法请求
            if (!req.decoderResult().isSuccess() || (!"websocket".equals(headers.get("Upgrade")))) {
                //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
                log.warn("非法连接, remote address:{}", NettyUtil.getRemoteAddress(ctx));
                NettyUtil.sendHttpErrResponse(channel, req, new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST
                ));
                return;
            }

            // 鉴权
            try {
                String token = headers.get(Sec_Protocol);
                R<UserDto> resp = remoteUserService.getUserInfoByToken(token);
                UserDto userDto = resp.getData();
                if (resp.getCode() != 200 || userDto == null) {
                    log.info("ws连接失败, 权限不足");
                    channel.writeAndFlush(new CloseWebSocketFrame());
                    channel.close();
                    return;
                }
                wsHandShake(req, channel, token).addListener(feature -> {
                    if (feature.isSuccess()) {
                        log.info("握手成功");
                        ClientCtxUtil.userConnect(userDto, channel);
                    } else {
                        log.error("握手失败");
                    }
                });
            } catch (Exception e) {
                log.error("鉴权失败, err:", e);
                channel.writeAndFlush(new CloseWebSocketFrame());
                channel.close();
            }
        } else {
            // 将消息传递给下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * ws 握手
     *
     * @param req     req
     * @param channel channel
     * @param token   token
     */
    private void wsHandShake(FullHttpRequest req, Channel channel, String token, UserDto userDto) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true, 5 * 1024 * 1024);
        WebSocketServerHandshaker handshake = wsFactory.newHandshaker(req);
        HttpHeaders headers = new DefaultHttpHeaders()
                .add(Sec_Protocol, token);
        if (handshake == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(channel);
        } else {
            handshake.handshake(channel, req, headers, channel.newPromise())
                    .addListener(feature -> {
                        if (feature.isSuccess()) {
                            log.info("握手成功");
                            ClientCtxUtil.userConnect(userDto, channel);
                        } else {
                            log.error("握手失败");
                        }
                    });
        }
    }

    private ChannelFuture wsHandShake(FullHttpRequest req, Channel channel, String token) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true, 5 * 1024 * 1024);
        WebSocketServerHandshaker handshake = wsFactory.newHandshaker(req);
        HttpHeaders headers = new DefaultHttpHeaders()
                .add(Sec_Protocol, token);
        if (handshake == null) {
            return WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(channel);
        }
        return handshake.handshake(channel, req, headers, channel.newPromise());
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + req.uri();
        return "ws://" + location;
    }


}
