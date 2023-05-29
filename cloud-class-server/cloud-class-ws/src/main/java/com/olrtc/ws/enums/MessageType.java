package com.olrtc.ws.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author njy
 * @since 2023/2/15 22:52
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    /**
     *
     */
    SDP(1, "媒体信息"),

    CHEAT(2, "聊天"),
    CHEAT_ROOM(3, "群聊"),

    COMMAND(4, "指令"),

    ;


    private final Integer value;

    @JsonValue
    private final String type;

}
