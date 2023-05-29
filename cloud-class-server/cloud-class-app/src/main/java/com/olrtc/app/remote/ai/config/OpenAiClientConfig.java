package com.olrtc.app.remote.ai.config;

import com.olrtc.app.remote.ai.OpenAiProxyService;
import com.olrtc.app.remote.ai.OpenAiService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author njy
 * @since 2023/3/31 11:10
 */
@Slf4j
@Configuration
public class OpenAiClientConfig {

    private static OpenAiProperties openAiProperties;
    private static final int DEFAULT_CONNECT_TIMEOUT = 20000;
    private static final int DEFAULT_READ_TIMEOUT = 25000;

    @Resource
    public void setRemoteRoomService(OpenAiProperties openAiProperties) {
        OpenAiClientConfig.openAiProperties = openAiProperties;
    }


    /**
     * open ai 远程调用基本参数
     *
     * @return org.springframework.web.service.invoker.HttpServiceProxyFactory
     * @author njy
     * @since 11:13 2023/3/31
     */
    public static HttpServiceProxyFactory getOpenAiFactory() {
        WebClient client = WebClient.builder()
                .baseUrl("baseUrl-proxy")
                .defaultHeader("Authorization", "Bearer " + openAiProperties.getToken())
                .build();
        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).blockTimeout(Duration.ofSeconds(60)).build();
    }


    @Bean
    OpenAiService openAiService() {
        return getOpenAiFactory().createClient(OpenAiService.class);
    }


    @Bean
    OpenAiProxyService openAiProxyService() {
        int connectTimeout = 20000; // 连接超时时间，单位毫秒
        int readTimeout = 25000; // 读取超时时间，单位毫秒

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));
        ClientHttpConnector httpConnector = new ReactorClientHttpConnector(HttpClient.from(tcpClient));

        WebClient client = WebClient.builder()
                .baseUrl(openAiProperties.getProxyUrl())
                .defaultHeader("Authorization", "Bearer " + openAiProperties.getToken())
                .clientConnector(httpConnector)
                .build();
        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client))
                .build()
                .createClient(OpenAiProxyService.class);
    }

}
