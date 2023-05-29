package com.olrtc.app.biz;

import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.dto.TeaStartLiveDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.dto.UserJoinLiveDto;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/13 15:40
 */
public interface LiveBiz {

    /**
     * 获取直播房间用户
     *
     * @param roomId roomId
     * @return java.util.Set<java.lang.String>
     */
    List<String> getUserNamesByRoomId(String roomId);

    /**
     * user join course live room
     *
     * @param courseId courseId
     * @return com.olrtc.app.model.dto.UserJoinLiveDto
     * @author njy
     * @since 16:48 2023/3/15
     */
    UserJoinLiveDto userJoinLive(Integer courseId);

    /**
     * teacher start live
     *
     * @param courseId courseId
     * @return com.olrtc.app.model.dto.TeaStartLiveDto
     * @author njy
     * @since 17:58 2023/3/15
     */
    TeaStartLiveDto teaStartLive(Integer courseId);

    /**
     * user exit live
     *
     * @param roomId roomId
     * @author njy
     * @since 10:02 2023/3/24
     */
    void userLeaveLive(String roomId);

    /**
     * tea stop live
     *
     * @param roomId roomId
     * @author njy
     * @since 10:02 2023/3/24
     */
    void teaStopLive(String roomId);

    /**
     * 获取直播间用户信息
     *
     * @param roomId roomId
     * @return java.util.List<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 10:40 2023/3/24
     */
    List<UserDto> getUsersByRoomId(String roomId);

    /**
     * @param roomId
     * @return void
     * @author njy
     * @since 16:39 2023/3/25
     */
    void teaJoinLive(String roomId);

    /**
     * @param roomId
     * @return com.olrtc.app.model.domain.LiveRoomInfo
     * @author njy
     * @since 17:37 2023/3/25
     */
    LiveRoomInfo getLiveRoomInfo(String roomId);

    /**
     * kick user
     *
     * @param roomId
     * @param userId
     * @return void
     * @author njy
     * @since 13:10 2023/3/27
     */
    boolean kickUser(String roomId, Integer userId);

    void updateLiveStatus(String roomId, Boolean camera, Boolean screen);
}
