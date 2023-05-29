package com.olrtc.app.service;

import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.Live;
import com.olrtc.app.model.entity.User;

import java.util.Set;

/**
 * @author njy
 * @since 2023/3/15 18:00
 */
public interface LiveRoomService {


    /**
     * 创建一个房间
     *
     * @param roomId
     * @return void
     * @author njy
     * @since 15:59 2023/3/25
     */
    void createLiveRoom(String roomId, Live live, String whiteboardUrl, CourseDto courseDto);


    /**
     * 销毁房间
     *
     * @param roomId
     * @return void
     * @author njy
     * @since 16:01 2023/3/25
     */
    void destroyLiveRoom(String roomId);

    /**
     * teacher start to live
     *
     * @param roomId
     * @return void
     * @author njy
     * @since 18:01 2023/3/15
     */
    void teaStartLive(String roomId, Integer userId);

    /**
     * teacher stop live
     *
     * @param roomId
     * @return void
     * @author njy
     * @since 18:03 2023/3/15
     */
    void stopLive(String roomId);


    /**
     * user join live
     *
     * @param roomId
     * @param userId
     * @return void
     * @author njy
     * @since 18:03 2023/3/15
     */
    void userJoinLive(String roomId, Integer userId);

    /**
     * user leave live
     *
     * @param roomId
     * @param userId
     * @return void
     * @author njy
     * @since 18:03 2023/3/15
     */
    void userLeaveLive(String roomId, Integer userId);


    /**
     * 获取直播房间用户
     *
     * @param roomId roomId
     * @return java.util.Set<java.lang.String>
     */
    Set<UserDto> getUserByRoomId(String roomId);

    /**
     * 是否开播
     *
     * @param roomId
     * @return boolean
     * @author njy
     * @since 21:03 2023/3/15
     */
    boolean isLive(String roomId);


    /**
     * @param roomId
     * @return com.olrtc.app.model.domain.LiveRoomInfo
     * @author njy
     * @since 17:38 2023/3/25
     */
    LiveRoomInfo getLiveRoomInfo(String roomId);
}
