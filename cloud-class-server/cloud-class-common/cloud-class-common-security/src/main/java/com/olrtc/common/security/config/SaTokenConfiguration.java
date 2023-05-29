package com.olrtc.common.security.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author njy
 * @since 2023/2/20 09:15
 */
@AutoConfiguration
public class SaTokenConfiguration {

    // Sa-Token 参数配置，参考文档：https://sa-token.cc
    // 此配置会覆盖 application.yml 中的配置
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName("token");
        config.setTimeout(3 * 24 * 60 * 60);
        config.setActivityTimeout(-1);
        config.setIsConcurrent(false);
        config.setIsReadHeader(true);
        config.setIsWriteHeader(true);
        config.setTokenStyle("simple-uuid");
        config.setIsLog(false);
        return config;
    }
}
