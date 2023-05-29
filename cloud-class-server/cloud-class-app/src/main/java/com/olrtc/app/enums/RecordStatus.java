package com.olrtc.app.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author njy
 * @since 2023/4/19 13:15
 */
@Getter
@AllArgsConstructor
public enum RecordStatus {
    /**
     *
     */
    INIT(1, "初始化"),
    START(2, "录制中"),
    END(3, "录制结束"),
    GEN(4, "视频生成");

    private final Integer value;
    @JsonValue
    private final String  type;

}