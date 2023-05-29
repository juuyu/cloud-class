package com.olrtc.app.config;

import com.olrtc.app.handler.TraceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class MyWebMvcConfigure implements WebMvcConfigurer {


    private final TraceInterceptor traceInterceptor;

    /**
     * 配置traceId请求
     *
     * @param registry registry
     * @author njy
     * @since 15:17 2022/9/22
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumConverter());
    }
}
