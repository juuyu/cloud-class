package com.olrtc.app.remote.ai.domain;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/3 16:42
 */
@Data
public class OpenAiProxyRespBody {
    private Integer code;
    private String  message;
    private Long    timestamp;
    private Object  data;
}
