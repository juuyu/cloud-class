package com.olrtc.ws.biz;

import com.olrtc.ws.domain.msg.ChatRespMessage;
import com.olrtc.ws.domain.msg.ChatSendMessage;

/**
 * @author njy
 * @since 2023/3/22 19:58
 */
public interface MsgPersistenceBiz {

    /**
     * 记录群聊消息
     *
     * @param chatSendMessage chatSendMessage
     * @param chatRespMessage chatRespMessage
     */
    void recordChatRoomMessage(ChatSendMessage chatSendMessage, ChatRespMessage chatRespMessage);
}
