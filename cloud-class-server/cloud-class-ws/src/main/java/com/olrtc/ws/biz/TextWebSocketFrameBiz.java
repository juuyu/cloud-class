package com.olrtc.ws.biz;

import com.olrtc.ws.domain.msg.ChatSendMessage;
import com.olrtc.ws.domain.ClientCtxInfo;
import com.olrtc.ws.domain.msg.CommandSendMsg;
import com.olrtc.ws.domain.msg.SdpSendMessage;

/**
 * @author njy
 * @since 2023/3/13 10:57
 */
public interface TextWebSocketFrameBiz {

    /**
     * 处理聊天信息
     *
     * @param clientCtxInfo   clientCtxInfo
     * @param chatSendMessage chatSendMessage
     */
    void handlerChatMessage(ClientCtxInfo clientCtxInfo, ChatSendMessage chatSendMessage);

    /**
     * 处理媒体信息交换
     *
     * @param clientCtxInfo  clientCtxInfo
     * @param sdpSendMessage sdpSendMessage
     */
    void handlerSdpMessage(ClientCtxInfo clientCtxInfo, SdpSendMessage sdpSendMessage);

    /**
     * 处理聊天信息(课堂聊天)
     *
     * @param clientCtxInfo   clientCtxInfo
     * @param chatSendMessage chatSendMessage
     */
    void handlerChatRoomMessage(ClientCtxInfo clientCtxInfo, ChatSendMessage chatSendMessage);

    /**
     * 指令消息
     *
     * @param clientCtxInfo  clientCtxInfo
     * @param commandSendMsg commandSendMsg
     */
    void handlerCommandMessage(ClientCtxInfo clientCtxInfo, CommandSendMsg commandSendMsg);
}
