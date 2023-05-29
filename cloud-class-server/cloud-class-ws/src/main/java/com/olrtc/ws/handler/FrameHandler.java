package com.olrtc.ws.handler;

import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.ws.biz.TextWebSocketFrameBiz;
import com.olrtc.ws.cache.ClientCtxUtil;
import com.olrtc.ws.domain.msg.ChatSendMessage;
import com.olrtc.ws.domain.msg.CommandRespMsg;
import com.olrtc.ws.domain.msg.CommandSendMsg;
import com.olrtc.ws.domain.msg.SdpSendMessage;
import com.olrtc.ws.domain.WebSocketMessage;
import com.olrtc.ws.enums.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author njy
 * @since 2023/3/12 17:45
 */
@Slf4j
@Component
public class FrameHandler {

    private static TextWebSocketFrameBiz textWebSocketFrameBiz;

    @Resource
    public void setTextWebSocketFrameBiz(TextWebSocketFrameBiz textWebSocketFrameBiz) {
        FrameHandler.textWebSocketFrameBiz = textWebSocketFrameBiz;
    }


    /**
     * 关闭链路的指令
     *
     * @param ctx        ctx
     * @param closeFrame closeFrame
     */
    public static void handlerCloseWebSocketFrame(ChannelHandlerContext ctx, CloseWebSocketFrame closeFrame) {
        log.info("handlerCloseWebSocketFrame() called with parameters => [ctx = {}], [closeFrame = {}]", ctx, closeFrame);
        ClientCtxUtil.userDisConnect(ctx.channel());
    }


    /**
     * 文本消息
     *
     * @param ctx       ctx
     * @param textFrame textFrame
     */
    public static void handlerTextWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame textFrame) {
        String message = textFrame.text();
        Channel channel = ctx.channel();
        if ("PING".equals(message)) {
            return;
        }


        WebSocketMessage webSocketMessage = JsonUtil.string2Obj(message, WebSocketMessage.class);
        if (webSocketMessage == null) {
            log.warn("消息不符合通讯格式, 无法解析, message:{}", message);
            return;
        }

        MessageType messageType = webSocketMessage.getMessageType();
        String dataJsonString = webSocketMessage.getDataJsonString();

        switch (messageType) {
            case SDP -> {
                // 处理sdp
                SdpSendMessage sdpSendMessage = JsonUtil.string2Obj(dataJsonString, SdpSendMessage.class);
                if (sdpSendMessage == null) {
                    log.warn("消息不符合通讯格式, 无法解析, message:{}", message);
                    return;
                }
                textWebSocketFrameBiz.handlerSdpMessage(ClientCtxUtil.getClientCtxInfo(channel), sdpSendMessage);
            }

            case CHEAT -> {
                // 处理chat
                ChatSendMessage chatSendMessage = JsonUtil.string2Obj(dataJsonString, ChatSendMessage.class);
                if (chatSendMessage == null) {
                    log.warn("消息不符合通讯格式, 无法解析, message:{}", message);
                    return;
                }
                textWebSocketFrameBiz.handlerChatMessage(ClientCtxUtil.getClientCtxInfo(channel), chatSendMessage);
            }


            case CHEAT_ROOM -> {
                // 群聊
                ChatSendMessage chatSendMessage = JsonUtil.string2Obj(dataJsonString, ChatSendMessage.class);
                if (chatSendMessage == null) {
                    log.warn("消息不符合通讯格式, 无法解析, message:{}", message);
                    return;
                }
                textWebSocketFrameBiz.handlerChatRoomMessage(ClientCtxUtil.getClientCtxInfo(channel), chatSendMessage);
            }



            case COMMAND -> {
                // 指令
                CommandSendMsg commandSendMsg = JsonUtil.string2Obj(dataJsonString, CommandSendMsg.class);
                if (commandSendMsg == null) {
                    log.warn("消息不符合通讯格式, 无法解析, message:{}", message);
                    return;
                }
                textWebSocketFrameBiz.handlerCommandMessage(ClientCtxUtil.getClientCtxInfo(channel), commandSendMsg);

            }

            default -> log.warn("没有此类消息, 无法处理 messageType:{}, message:{}", messageType, message);

        }


    }


    public static void handlerBinaryWebSocketFrame(ChannelHandlerContext ctx, BinaryWebSocketFrame binaryFrame) {
        log.info("handlerBinaryWebSocketFrame() called with parameters => [ctx = {}], [binaryFrame = {}]", ctx, binaryFrame);
    }


    public static void handlerPingWebSocketFrame(ChannelHandlerContext ctx, PingWebSocketFrame pingFrame) {
//        log.info("handlerPingWebSocketFrame() called with parameters => [ctx = {}], [pingFrame = {}]", ctx, pingFrame);
//        PongWebSocketFrame pongWebSocketFrame = new PongWebSocketFrame(pingFrame.content().retain());
//        ctx.channel().writeAndFlush(pongWebSocketFrame);
    }
}
