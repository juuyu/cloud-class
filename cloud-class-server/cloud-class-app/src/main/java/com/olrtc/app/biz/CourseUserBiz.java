package com.olrtc.app.biz;

import com.olrtc.app.model.dto.UserDto;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:21
 */
public interface CourseUserBiz {

    /**
     * 获取加入课程的人员
     * @author njy
     * @since 22:23 2023/3/14
     * @param pageQuery
     * @param courseId
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.UserDto>
     */
    TableDataInfo<UserDto> listJoinCourseUsers(PageQuery pageQuery, Integer courseId);


    List<UserDto> listJoinCourseUserList(Integer courseId);

    void kickUserInCourse(Integer courseId, Integer userId);

    void exitCourse(Integer courseId, Integer userId);
}
