package com.olrtc.app.remote.ai.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author njy
 * @since 2023/3/31 11:16
 */
@Getter
@AllArgsConstructor
public enum AiModel {
    // https://zhuanlan.zhihu.com/p/608309509
    /**
     *
     */
    DAV_003("text-davinci-003", "达芬奇003"),

    GPT_035("gpt-3.5-turbo", "gpt-3.5-turbo"),

    CODE_002("code-davinci-002", "code-davinci-002");

    @JsonValue
    private final String model;

    private final String desc;


}
