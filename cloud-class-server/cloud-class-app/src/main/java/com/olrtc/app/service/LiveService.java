package com.olrtc.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.entity.Live;

/**
 * @author yu
 * @description 针对表【t_live】的数据库操作Service
 * @createDate 2023-03-15 17:08:48
 */
public interface LiveService extends IService<Live> {

    /**
     * 根据课程id获取直播信息
     *
     * @param courseId
     * @return com.olrtc.app.model.entity.Live
     * @author njy
     * @since 21:42 2023/3/15
     */
    Live getLiveByCourseId(Integer courseId);


    /**
     * 根据 roomCode 查询live info
     *
     * @param roomCode
     * @return com.olrtc.app.model.entity.Live
     * @author njy
     * @since 10:07 2023/3/24
     */
    Live getLiveByRoomCode(String roomCode);

}
