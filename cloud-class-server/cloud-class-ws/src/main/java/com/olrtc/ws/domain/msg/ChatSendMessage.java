package com.olrtc.ws.domain.msg;

import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/12 17:37
 */
@Data
public class ChatSendMessage {

    // 房间号
    private String roomId;

    // 是否发给全部人
    private boolean atAll;

    // 发送对象帐号
    private String toUserName;

    // 发送时间
    private String sendTimeTimestamp;

    // 消息内容
    private String msg;


}
