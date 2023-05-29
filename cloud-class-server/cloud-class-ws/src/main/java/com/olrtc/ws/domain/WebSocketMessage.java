package com.olrtc.ws.domain;

import com.olrtc.ws.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author njy
 * @since 2023/2/15 22:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息内容
     */
    private String dataJsonString;

}
