package com.olrtc.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author njy
 * @since 2023/3/10 10:36
 */
@Getter
@AllArgsConstructor
public enum JoinType {
    /**
     *
     */
    TEA_INVITE(1, "老师邀请"),
    USER_APPLY(2, "用户申请");

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String  type;


}
