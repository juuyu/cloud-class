package com.olrtc.app.web.api;

import com.olrtc.app.biz.LiveBiz;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/13 15:38
 */
@Slf4j
@RestController
@RequestMapping("api/live")
@RequiredArgsConstructor
public class LiveApi {

    private final LiveBiz liveBiz;

    @GetMapping("{roomId}")
    public R<List<String>> getUserNamesByRoomId(@PathVariable("roomId") String roomId) {
        log.info("getUserNamesByRoomId() called with parameters => [roomId = {}]",roomId);
        List<String> names = liveBiz.getUserNamesByRoomId(roomId);
        return names.isEmpty() ? R.fail() : R.ok(names);
    }


    @GetMapping("update")
    public R<Void> updateLiveStatus(String roomId, Boolean camera, Boolean screen) {
        log.info("updateLiveStatus() called with parameters => [roomId = {}], [camera = {}], [screen = {}]", roomId, camera, screen);
        liveBiz.updateLiveStatus(roomId, camera, screen);
        return R.ok("更新成功");
    }


}
