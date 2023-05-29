package com.olrtc.ws.remote;

import com.olrtc.common.core.domain.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/13 11:19
 */
@HttpExchange("/live")
public interface RemoteRoomService {

    /**
     * 获取房间内的用户帐号
     *
     * @param roomId roomId
     * @return reactor.core.publisher.Flux<com.olrtc.common.core.domain.R < java.lang.String [ ]>>
     */
    @GetExchange("{roomId}")
    Flux<R<List<String>>> getUserNamesByRoomId(@PathVariable("roomId") String roomId);


    /**
     * 更新直播状态
     *
     * @param roomId roomId
     * @param camera camera
     * @param screen screen
     * @return reactor.core.publisher.Mono<java.lang.Void>
     */
    @GetExchange("/update")
    Mono<R<Void>> updateLiveStatus(@RequestParam("roomId") String roomId,
                                   @RequestParam("camera") Boolean camera,
                                   @RequestParam("screen") Boolean screen);
}
