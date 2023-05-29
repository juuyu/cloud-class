package com.olrtc.app.remote.ai.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * @author njy
 * @since 2023/4/3 16:42
 */
@Data
@Accessors(chain = true)
public class OpenAiProxyReqBody {
    private String apiKey    = "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";
    private String sessionId = UUID.randomUUID().toString();
    private String content;
}
