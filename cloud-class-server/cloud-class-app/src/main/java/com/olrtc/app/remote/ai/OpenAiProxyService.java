package com.olrtc.app.remote.ai;

import com.olrtc.app.remote.ai.domain.OpenAiProxyReqBody;
import com.olrtc.app.remote.ai.domain.OpenAiProxyRespBody;
import com.olrtc.app.remote.ai.domain.OpenAiReqBody;
import com.olrtc.app.remote.ai.domain.OpenAiRespBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * open ai代理转发服务
 *
 * @author njy
 * @since 2023/4/3 16:24
 */
@HttpExchange
public interface OpenAiProxyService {
    /**
     * open ai api(废弃)
     *
     * @param openAiProxyReqBody openAiProxyReqBody
     * @return reactor.core.publisher.Mono<com.olrtc.app.remote.ai.domain.OpenAiRespBody>
     * @author njy
     * @since 11:33 2023/3/31
     */
    @Deprecated
    @PostExchange("pro/chat/completions")
    OpenAiProxyRespBody invokeOpenAiService(@RequestBody OpenAiProxyReqBody openAiProxyReqBody);


    /**
     * open ai  completions
     *
     * @param openAiReqBody openAiReqBody
     * @return com.olrtc.app.remote.ai.domain.OpenAiRespBody
     * @author njy
     * @since 18:50 2023/4/8
     */
    @PostExchange("v1/completions")
    OpenAiRespBody invokeOpenAiCompletions(@RequestBody OpenAiReqBody openAiReqBody);


    /**
     * 查询余额
     *
     * @return com.olrtc.app.remote.ai.domain.OpenAiProxyRespBody
     * @author njy
     * @since 19:57 2023/4/8
     */
    @GetExchange("pro/balance")
    OpenAiProxyRespBody invokeOpenAiBalance(@RequestParam("apiKey") String apiKey);

}
