package com.olrtc.ws.biz.impl;

import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.redis.utils.RedisUtil;
import com.olrtc.ws.biz.MsgPersistenceBiz;
import com.olrtc.ws.domain.msg.ChatRespMessage;
import com.olrtc.ws.domain.msg.ChatSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njy
 * @since 2023/3/22 19:58
 */
@Slf4j
@Service
public class MsgPersistenceBizImpl implements MsgPersistenceBiz {

    private static final String CHAT_ROOM_MSG_KEY = "list:message:chat:room:";

    @Override
    public void recordChatRoomMessage(ChatSendMessage chatSendMessage, ChatRespMessage chatRespMessage) {
        Thread.startVirtualThread(
                () -> {
                    String key = CHAT_ROOM_MSG_KEY + chatSendMessage.getRoomId();
                    List<String> list = new ArrayList<>();
                    list.add(JsonUtil.obj2String(chatRespMessage));
                    RedisUtil.setCacheList(key, list);
                });
    }
}
