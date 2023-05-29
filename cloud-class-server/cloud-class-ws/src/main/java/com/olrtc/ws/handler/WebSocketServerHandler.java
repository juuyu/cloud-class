package com.olrtc.ws.handler;

import com.olrtc.ws.cache.ClientCtxUtil;
import com.olrtc.ws.util.NettyUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * @author njy
 * @since 2023/2/1 15:14
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    /**
     * 读取客户端信息
     *
     * @param ctx   ctx
     * @param frame frame
     * @author njy
     * @since 13:51 2022/11/8
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {

        switch (frame) {
            // 判断是否关闭链路的指令
            case CloseWebSocketFrame closeFrame -> FrameHandler.handlerCloseWebSocketFrame(ctx, closeFrame);

            // ping
            case PingWebSocketFrame pingFrame -> FrameHandler.handlerPingWebSocketFrame(ctx, pingFrame);

            // 文本消息
            case TextWebSocketFrame textFrame -> FrameHandler.handlerTextWebSocketFrame(ctx, textFrame);

            // 二进制消息
            case BinaryWebSocketFrame binaryFrame -> FrameHandler.handlerBinaryWebSocketFrame(ctx, binaryFrame);

            default -> log.warn("暂不支持此类消息, frame:{}",frame);
        }

    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    /**
     * 客户端上线的时候调用
     *
     * @param ctx ctx
     * @author njy
     * @since 13:52 2022/11/8
     */
    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        log.info("remoteAddress:{}, 已连接", NettyUtil.getRemoteAddress(ctx));
        super.channelActive(ctx);
    }

    /**
     * 客户端掉线的时候调用
     *
     * @param ctx ctx
     * @author njy
     * @since 13:53 2022/11/8
     */
    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        log.info("remoteAddress:{}, 已退出", NettyUtil.getRemoteAddress(ctx));
        super.channelInactive(ctx);
    }

    /**
     * 异常发生时候调用
     *
     * @param ctx   ctx
     * @param cause cause
     * @author njy
     * @since 13:53 2022/11/8
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("remoteAddress:{} disconnect with exception:{}", NettyUtil.getRemoteAddress(ctx), cause.getMessage());
        ClientCtxUtil.userDisConnect(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }


}


