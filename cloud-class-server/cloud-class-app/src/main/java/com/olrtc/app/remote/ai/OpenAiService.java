package com.olrtc.app.remote.ai;

import com.olrtc.app.remote.ai.domain.OpenAiReqBody;
import com.olrtc.app.remote.ai.domain.OpenAiRespBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author njy
 * @since 2023/3/31 11:11
 */
@HttpExchange("v1")
public interface OpenAiService {

    /**
     * open ai api
     *
     * @param openAiReqBody openAiReqBody
     * @return reactor.core.publisher.Mono<com.olrtc.app.remote.ai.domain.OpenAiRespBody>
     * @author njy
     * @since 11:33 2023/3/31
     */
    @PostExchange("completions")
    OpenAiRespBody invokeOpenAiService(@RequestBody OpenAiReqBody openAiReqBody);


//    @GetExchange("models")
//    Flux<> listOpenAiModels();


}
