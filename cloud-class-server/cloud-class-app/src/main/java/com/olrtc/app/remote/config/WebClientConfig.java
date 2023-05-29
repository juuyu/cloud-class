package com.olrtc.app.remote.config;

import com.olrtc.app.remote.RemoteFfmpegService;
import com.olrtc.app.remote.RemoteWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author njy
 * @since 2023/3/8 23:18
 */
@Slf4j
@Configuration
public class WebClientConfig {

    public static HttpServiceProxyFactory getWsFactory() {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:8082/api")
                .build();
        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
    }


    @Bean
    RemoteWsService remoteUserService() {
        return getWsFactory().createClient(RemoteWsService.class);
    }


    @Bean
    RemoteFfmpegService remoteFfmpegService() {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:7070")
                .build();
        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client))
                .build()
                .createClient(RemoteFfmpegService.class);
    }


}
