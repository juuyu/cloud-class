package com.olrtc.app.biz;

import com.olrtc.app.model.dto.UserTodayCourseDto;
import com.olrtc.app.model.dto.UserTodayCourseJobDto;

/**
 * @author njy
 * @since 2023/5/4 14:24
 */
public interface WorkBiz {
    /**
     * 获取用户今日课程
     *
     * @param userId userId
     * @return com.olrtc.app.model.dto.UserTodayCourseDto
     */
    UserTodayCourseDto getUserTodayCourse(Integer userId);

    /**
     * 获取用户今日课程(多线程)
     *
     * @param userId userId
     * @return com.olrtc.app.model.dto.UserTodayCourseDto
     */
    UserTodayCourseDto getUserTodayCourseAsync(Integer userId);

    /**
     * 获取用户今日作业
     *
     * @param userId userId
     * @return com.olrtc.app.model.dto.UserTodayCourseJobDto
     */
    UserTodayCourseJobDto getUserTodayCourseJob(Integer userId);
}
