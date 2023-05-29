package com.olrtc.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "live")
public class LiveServerProperties {


    private String publishUrl;


    private String liveUrl;

}
