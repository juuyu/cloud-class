package com.olrtc.ws.remote.config;

import com.olrtc.ws.remote.RemoteRoomService;
import com.olrtc.ws.remote.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 远程http调用配置
 *
 * @author njy
 * @since 2023/2/20 14:22
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${remote.url}")
    private String baseUrl;

    public HttpServiceProxyFactory getDefaultFactory() {
        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
    }


    @Bean
    RemoteUserService remoteUserService() {
        return getDefaultFactory().createClient(RemoteUserService.class);
    }

    @Bean
    RemoteRoomService remoteRoomService() {
        return getDefaultFactory().createClient(RemoteRoomService.class);
    }

}
