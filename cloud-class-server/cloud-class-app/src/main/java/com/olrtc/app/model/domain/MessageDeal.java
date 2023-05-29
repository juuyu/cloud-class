package com.olrtc.app.model.domain;

import com.olrtc.app.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author njy
 * @since 2023/4/11 14:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDeal {
    private MessageType messageType;

    private String paramJson;
}
