package com.olrtc.app.remote.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {


    private String token;
    private String baseUrl;
    private String proxyUrl;

}
