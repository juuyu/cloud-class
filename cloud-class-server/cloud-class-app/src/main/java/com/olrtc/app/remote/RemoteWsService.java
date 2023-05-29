package com.olrtc.app.remote;

import com.olrtc.common.core.domain.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

/**
 * @author njy
 * @since 2023/3/27 13:34
 */
@HttpExchange("/notice")
public interface RemoteWsService {

    @GetExchange("kick/{userName}")
    R<Boolean> noticeUserExit(@PathVariable("userName") String userName);

}
