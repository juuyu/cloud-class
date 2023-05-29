package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.entity.CourseUser;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course_user】的数据库操作Service
 * @createDate 2023-02-22 10:56:10
 */
public interface CourseUserService extends IService<CourseUser> {

    List<CourseUser> getCourseUserByUserId(Integer userId);
    /**
     * 获取用户课程关系
     *
     * @param courseId
     * @param userId
     * @return com.olrtc.app.model.entity.CourseUser
     * @author njy
     * @since 11:47 2023/3/15
     */
    CourseUser getUserJoinCourseRecord(Integer courseId, Integer userId);


    /**
     * 获取加入课程的用户课程关系
     *
     * @param courseId
     * @return java.util.List<com.olrtc.app.model.entity.CourseUser>
     * @author njy
     * @since 11:47 2023/3/15
     */
    List<CourseUser> getCourseUsersByCourseId(Integer courseId);


    /**
     * 判断用户是否加入课程
     *
     * @param userId
     * @param courseId
     * @return boolean
     * @author njy
     * @since 11:47 2023/3/15
     */
    boolean userIsJoinCourse(Integer userId, Integer courseId);

    /**
     * 用户同意加入课程
     *
     * @param courseUser
     * @return boolean
     * @author njy
     * @since 15:19 2023/4/11
     */
    boolean stuCheckToJoinCourse(CourseUser courseUser, boolean checkRes);

    /**
     * 老师同意用户加入课程
     *
     * @param courseUser
     * @return boolean
     * @author njy
     * @since 15:19 2023/4/11
     */
    boolean teaCheckToJoinCourse(CourseUser courseUser, boolean checkRes);


}
