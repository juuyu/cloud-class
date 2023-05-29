package com.olrtc.app.web;

import com.olrtc.app.biz.LiveBiz;
import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.dto.TeaStartLiveDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.dto.UserJoinLiveDto;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/13 16:01
 */
@Slf4j
@RestController
@RequestMapping("live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveBiz liveBiz;

    /**
     * @param roomId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserJoinLiveDto>
     * @author njy
     * @since 17:36 2023/3/25
     */
    @GetMapping("info")
    public R<LiveRoomInfo> getLiveRoomInfo(String roomId) {
        LiveRoomInfo liveRoomInfo = liveBiz.getLiveRoomInfo(roomId);
        return R.ok("ok", liveRoomInfo);
    }

    /**
     * user join course live
     *
     * @param courseId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserJoinLiveDto>
     * @author njy
     * @since 16:48 2023/3/15
     */
    @GetMapping("stu/join")
    public R<UserJoinLiveDto> userJoinLive(Integer courseId) {
        UserJoinLiveDto userJoinLiveDto = liveBiz.userJoinLive(courseId);
        return R.ok("join success", userJoinLiveDto);
    }

    /**
     * user leave course live
     *
     * @param roomId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 09:51 2023/3/24
     */
    @GetMapping("stu/leave")
    public R<Void> userLeaveLive(String roomId) {
        liveBiz.userLeaveLive(roomId);
        return R.ok("leave success");
    }

    /**
     * teacher start live
     *
     * @param courseId courseId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.TeaStartLiveDto>
     * @author njy
     * @since 17:58 2023/3/15
     */
    @GetMapping("tea/start")
    public R<TeaStartLiveDto> teaStartLive(Integer courseId) {
        TeaStartLiveDto teaStartLiveDto = liveBiz.teaStartLive(courseId);
        return R.ok("live success", teaStartLiveDto);
    }

    /**
     * @param roomId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:39 2023/3/25
     */
    @GetMapping("tea/join")
    public R<Void> teaJoinLive(String roomId) {
        liveBiz.teaJoinLive(roomId);
        return R.ok("ok");
    }

    /**
     * tea stop course live
     *
     * @param roomId roomId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 09:52 2023/3/24
     */
    @GetMapping("tea/stop")
    public R<Void> teaStopLive(String roomId) {
        liveBiz.teaStopLive(roomId);
        return R.ok("stop success");
    }


    /**
     * 获取直播间用户
     *
     * @param roomId roomId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 10:39 2023/3/24
     */
    @GetMapping("users")
    public R<List<UserDto>> getLiveRoomUsers(String roomId) {
        log.info("getLiveRoomUsers() called with parameters => [roomId = {}]",roomId);
        List<UserDto> users = liveBiz.getUsersByRoomId(roomId);
        return R.ok("ok", users);
    }


    /**
     * kick user
     *
     * @param roomId roomId
     * @param userId userId
     * @return com.olrtc.common.core.domain.R<java.lang.Boolean>
     * @author njy
     * @since 13:10 2023/3/27
     */
    @GetMapping("kick")
    public R<Boolean> kickRoomUser(String roomId, Integer userId) {
        log.info("kickRoomUser() called with parameters => [roomId = {}], [userId = {}]",roomId, userId);
        boolean f = liveBiz.kickUser(roomId, userId);
        return R.ok("ok", f);
    }


}
