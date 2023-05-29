package com.olrtc.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author njy
 * @since 2023/2/17 17:38
 */
@Getter
@AllArgsConstructor
public enum UserType {
    /**
     *
     */
    ADMIN(1, "管理员"),
    TEACHER(2, "教师"),
    STUDENT(3, "学生");

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String  type;

    public static UserType getUserType(Integer val) {
        for (UserType type : values()) {
            if (Objects.equals(type.getValue(), val)) {
                return type;
            }
        }
        throw new RuntimeException("'UserType' not found By " + val);
    }
}