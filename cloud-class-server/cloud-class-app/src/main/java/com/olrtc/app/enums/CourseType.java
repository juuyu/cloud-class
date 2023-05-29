package com.olrtc.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author njy
 * @since 2023/2/27 23:53
 */
@Getter
@AllArgsConstructor
public enum CourseType {
    /**
     *
     */
    CLASS_COURSE(1, "班级课"),
    PRIVATE_COURSE(2, "私教课"),
    PUBLIC_COURSE(3, "公开课");

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String  type;

}