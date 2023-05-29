package com.olrtc.ws.domain.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author njy
 * @since 2023/3/13 09:18
 */
@Data
@Accessors(chain = true)
public class ChatRespMessage{

    // 发送人帐号
    private String sendUserName;

    // 发送人姓名
    private String sendUserRealName;

    // 发送人头像
    private String avatar;

    // 发送时间
    private String sendTimeTimestamp;

    // 消息内容
    private String msg;

}
