package com.olrtc.common.security.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 权限安全配置
 *
 * @author Lion Li
 */
@AutoConfiguration
public class SecurityConfiguration implements WebMvcConfigurer {
    private static final List<String> EXCLUDE_PATH = Arrays.asList("/login", "/register");

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH)
        ;
    }

}
