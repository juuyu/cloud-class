package com.olrtc.app.remote;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

/**
 * @author njy
 * @since 2023/3/8 23:21
 */
@HttpExchange("/video")
public interface RemoteFfmpegService {

    /**
     * 合成视频并上传
     *
     * @param recordId recordId
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @author njy
     * @since 17:14 2023/4/19
     */
    @GetExchange("/merge/{recordId}")
    Mono<String> mergeVideo(@PathVariable("recordId") String recordId);

}
