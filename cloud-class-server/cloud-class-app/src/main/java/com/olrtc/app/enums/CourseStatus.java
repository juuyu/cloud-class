package com.olrtc.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author njy
 * @since 2023/2/27 23:50
 */
@Getter
@AllArgsConstructor
public enum CourseStatus {
    /**
     *
     */
    NORMAL(1, "进行中"),
    EXPIRE(2, "已完结")
    ;

    private final Integer value;
    @JsonValue
    private final String  type;

}
