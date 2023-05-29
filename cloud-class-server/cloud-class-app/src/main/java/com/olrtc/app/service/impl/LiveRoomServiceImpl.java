package com.olrtc.app.service.impl;

import cn.hutool.core.convert.Convert;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.Live;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.service.LiveRoomService;
import com.olrtc.app.service.MessageService;
import com.olrtc.app.service.UserService;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author njy
 * @since 2023/3/15 18:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveRoomServiceImpl implements LiveRoomService {
    private final UserService    userService;
    private final MessageService messageService;

    @Override
    public void createLiveRoom(String roomId, Live live, String whiteboardUrl, CourseDto courseDto) {
        String key = RedisCacheKey.LIVE.ROOM_INFO + roomId;
        Boolean hasKey = RedisUtil.hasKey(key);
        if (hasKey) {
            RedisUtil.deleteObject(key);
        }
        User user = userService.getById(courseDto.getTeacherId());
        UserDto userDto = Convert.convert(UserDto.class, user);
        LiveRoomInfo liveRoomInfo = new LiveRoomInfo()
                .setRoomID(roomId)
                .setWhiteboardUrl(whiteboardUrl)
                .setLiveInfo(live)
                .setCourseDto(courseDto)
                .setTeacherInfo(userDto)
                .setLive(false)
                .setCamera(true)
                .setScreen(false)
                .setRecordId(UUID.randomUUID().toString().replace("-", ""));
        RedisUtil.setCacheObject(key, liveRoomInfo);
    }

    @Override
    public void destroyLiveRoom(String roomId) {
        String key = RedisCacheKey.LIVE.ROOM_INFO + roomId;
        Boolean hasKey = RedisUtil.hasKey(key);
        if (hasKey) {
            RedisUtil.deleteObject(key);
        }
        stopLive(roomId);
    }

    @Override
    public void teaStartLive(String roomId, Integer userId) {
        String infoKey = RedisCacheKey.LIVE.ROOM_INFO + roomId;
        LiveRoomInfo liveRoomInfo = RedisUtil.getCacheObject(infoKey);
        if (liveRoomInfo != null) {
            liveRoomInfo.setStart(new Date());
            liveRoomInfo.setLive(true);
            RedisUtil.setCacheObject(infoKey, liveRoomInfo);
        }
        messageService.liveNoticeMessage(liveRoomInfo);

        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        Set<UserDto> users = new HashSet<>();
        UserDto userDto = Convert.convert(UserDto.class, userService.getById(userId));
        if (userDto != null) {
            users.add(userDto);
            RedisUtil.setCacheSet(key, users);
        }

    }

    @Override
    public void stopLive(String roomId) {
        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        Boolean hasKey = RedisUtil.hasKey(key);
        if (hasKey) {
            RedisUtil.deleteObject(key);
        }
    }

    @Override
    public void userJoinLive(String roomId, Integer userId) {
        log.info("userJoinLive() called with parameters => [roomId = {}], [userId = {}]", roomId, userId);
        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        Boolean hasKey = RedisUtil.hasKey(key);
        if (hasKey) {
            Set<UserDto> users = new HashSet<>();
            UserDto userDto = Convert.convert(UserDto.class, userService.getById(userId));
            if (userDto != null) {
                users.add(userDto);
                RedisUtil.setCacheSet(key, users);
            }

        }
    }

    @Override
    public void userLeaveLive(String roomId, Integer userId) {
        log.info("userLeaveLive() called with parameters => [roomId = {}], [userId = {}]", roomId, userId);
        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        Boolean hasKey = RedisUtil.hasKey(key);
        if (hasKey) {
            User user = userService.getById(userId);
            if (user != null) {
                Set<UserDto> users = RedisUtil.getCacheSet(key);
                users.remove(Convert.convert(UserDto.class, user));
                RedisUtil.deleteObject(key);
                RedisUtil.setCacheSet(key, users);
            }

        }
    }

    @Override
    public Set<UserDto> getUserByRoomId(String roomId) {
        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        return RedisUtil.getCacheSet(key);
    }

    @Override
    public boolean isLive(String roomId) {
        String key = RedisCacheKey.LIVE.ROOM_USER + roomId;
        return RedisUtil.hasKey(key);
    }

    @Override
    public LiveRoomInfo getLiveRoomInfo(String roomId) {
        String key = RedisCacheKey.LIVE.ROOM_INFO + roomId;
        return RedisUtil.getCacheObject(key);
    }
}
