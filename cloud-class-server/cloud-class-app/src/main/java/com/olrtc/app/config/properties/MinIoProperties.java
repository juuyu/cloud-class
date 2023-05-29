package com.olrtc.app.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jmj
 * @since 2022-5-16 0016 20:03
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinIoProperties {

    /**
     * minio地址+端口号
     */
    private String endpoint;

    /**
     * minio用户名
     */
    private String accessKey;

    /**
     * minio密码
     */
    private String secretKey;

    /**
     * 本地MinIO访问url
     */
    private String baseUrl;

    /**
     * 文件默认桶
     */
    private String commonBucket;
}
