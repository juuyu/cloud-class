package com.olrtc.app.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.olrtc.app.biz.LiveBiz;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.dto.TeaStartLiveDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.dto.UserJoinLiveDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.Live;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.entity.Video;
import com.olrtc.app.remote.RemoteWsService;
import com.olrtc.app.service.*;
import com.olrtc.app.utils.ExcalidrawUtil;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.redis.utils.RedisUtil;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author njy
 * @since 2023/3/13 15:43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveBizImpl implements LiveBiz {

    private final CourseService     courseService;
    private final CourseUserService courseUserService;
    private final LiveRoomService   liveRoomService;
    private final LiveService       liveService;
    private final UserService       userService;
    private final RemoteWsService   remoteWsService;
    private final VideoService      videoService;

    @Override
    public List<String> getUserNamesByRoomId(String roomId) {
        Set<UserDto> users = liveRoomService.getUserByRoomId(roomId);
        return users.stream()
                .map(UserDto::getUserName)
                .toList();
    }

    @Override
    public UserJoinLiveDto userJoinLive(Integer courseId) {
        if (courseId == null) {
            throw new BizException("courseId can't be null");
        }
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("user is not login");
        }
        boolean b = courseUserService.userIsJoinCourse(userId, courseId);
        if (!b) {
            throw new BizException("you are not in the course, can't join live");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BizException("course is null");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期");
        }
        Live live = liveService.getLiveByCourseId(course.getId());
        if (live == null) {
            throw new BizException("直播信息不存在");
        }
        boolean isLive = liveRoomService.isLive(live.getRoomId());
        if (!isLive) {
            throw new BizException("当前课程未开播");
        }

        UserJoinLiveDto userJoinLiveDto = new UserJoinLiveDto()
                .setRoomId(live.getRoomId())
                .setCourseId(live.getCourseId())
                .setUserVideoPlay(live.getUserVideoPlay())
                .setScreenVideoPlay(live.getScreenVideoPlay());

        liveRoomService.userJoinLive(live.getRoomId(), userId);
        return userJoinLiveDto;
    }


    @Override
    public TeaStartLiveDto teaStartLive(Integer courseId) {
        if (courseId == null) {
            throw new BizException("courseId can't be null");
        }
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("user is not login");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BizException("course is null");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期");
        }
        if (!userId.equals(course.getTeacherId())) {
            throw new BizException("你不是该课程的老师，无法开播");
        }
        Live live = liveService.getLiveByCourseId(course.getId());
        if (live == null) {
            throw new BizException("直播信息不存在");
        }
        String whiteboardUrl = ExcalidrawUtil.genRoomWhiteboardUrl(live.getRoomId());
        TeaStartLiveDto teaStartLiveDto = new TeaStartLiveDto()
                .setRoomId(live.getRoomId())
                .setWhiteboardUrl(whiteboardUrl)
                .setCourseId(live.getCourseId())
                .setUserVideoPublish(live.getUserVideoPublish())
                .setScreenVideoPublish(live.getScreenVideoPublish());

        liveRoomService.createLiveRoom(live.getRoomId(), live, whiteboardUrl, courseService.convertToDTO(course));
        return teaStartLiveDto;
    }

    @Override
    public void userLeaveLive(String roomId) {
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("user is not login");
        }
        liveRoomService.userLeaveLive(roomId, userId);
    }

    @Override
    public void teaStopLive(String roomId) {
        Live live = liveService.getLiveByRoomCode(roomId);
        if (live == null) {
            throw new BizException("live is null");
        }
        Course course = courseService.getById(live.getCourseId());
        if (course == null) {
            throw new BizException("course is null");
        }
        LiveRoomInfo liveRoomInfo = liveRoomService.getLiveRoomInfo(roomId);
        liveRoomInfo.setEnd(new Date());
        CompletableFuture.runAsync(() -> {
            String recordId = liveRoomInfo.getRecordId();
            String m3U8FileUrl = videoService.genM3U8File(recordId);
            if (m3U8FileUrl != null) {
                Video video = new Video();
                video.setRecordId(recordId);
                video.setUserId(course.getTeacherId());
                video.setVideoName(DateUtil.formatDateTime(liveRoomInfo.getEnd()) + " 录播");
                video.setRecordStart(liveRoomInfo.getStart());
                video.setRecordEnd(liveRoomInfo.getEnd());
                video.setVideoCoverLink("http://localhost:9000/cloud-class/icon/playback.svg");
                video.setVideoFileLink(m3U8FileUrl);
                video.setOpen(false);
                video.setWatchCount(0);

                log.info(JsonUtil.obj2String(video));
                boolean save = videoService.save(video);
                if (save)
                    log.info("录制信息保存成功 => [recordId = {}]", recordId);
                else
                    log.error("录制信息保存失败 => [recordId = {}]", recordId);
            }
        });
        liveRoomService.destroyLiveRoom(roomId);
    }

    @Override
    public List<UserDto> getUsersByRoomId(String roomId) {
        log.info("getUsersByRoomId() called with parameters => [roomId = {}]", roomId);
        Set<UserDto> users = liveRoomService.getUserByRoomId(roomId);
        LiveRoomInfo liveRoomInfo = liveRoomService.getLiveRoomInfo(roomId);
        String tea = liveRoomInfo.getTeacherInfo().getUserName();
        return users.stream()
                .filter(userDto -> !tea.equals(userDto.getUserName()))
                .toList();
    }

    @Override
    public void teaJoinLive(String roomId) {
        liveRoomService.teaStartLive(roomId, LoginUtil.getUserId());
    }

    @Override
    public LiveRoomInfo getLiveRoomInfo(String roomId) {
        return liveRoomService.getLiveRoomInfo(roomId);
    }

    @Override
    public boolean kickUser(String roomId, Integer userId) {
        log.info("kickUser() called with parameters => [roomId = {}], [userId = {}]", roomId, userId);
        Integer currUserId = LoginUtil.getUserId();
        if (currUserId == null) {
            throw new BizException("user is not login");
        }
        LiveRoomInfo liveRoomInfo = liveRoomService.getLiveRoomInfo(roomId);
        Integer id = liveRoomInfo.getTeacherInfo().getId();
        if (!id.equals(currUserId)) {
            throw new BizException("你不是该课程的老师，没有权限操作");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException("id= {" + userId + "} 的用户不存在");
        }
        R<Boolean> r = remoteWsService.noticeUserExit(user.getUserName());
        if (r.getCode() == 200 && r.getData()) {
            liveRoomService.userLeaveLive(roomId, userId);
            return true;
        } else {
            throw new BizException("踢人失败, 请检查你的网络");
        }
    }

    @Override
    public void updateLiveStatus(String roomId, Boolean camera, Boolean screen) {
        LiveRoomInfo liveRoomInfo = liveRoomService.getLiveRoomInfo(roomId);
        if (liveRoomInfo != null) {
            liveRoomInfo.setCamera(camera);
            liveRoomInfo.setScreen(screen);
            String key = RedisCacheKey.LIVE.ROOM_INFO + roomId;
            RedisUtil.setCacheObject(key, liveRoomInfo);
        }
    }
}
