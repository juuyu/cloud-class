package com.olrtc.app.biz;

import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.CourseSearchDto;
import com.olrtc.app.model.param.CheckToJoinCourseParam;
import com.olrtc.app.model.param.CourseAddParam;
import com.olrtc.app.model.param.CourseInviteParam;
import com.olrtc.app.model.param.CourseUpdateParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/1 09:10
 */
public interface CourseBiz {

    /**
     * 用户创建课程
     *
     * @param courseAddParam courseAddParam
     * @return boolean
     */
    @Transactional
    boolean createCourse(CourseAddParam courseAddParam);


    /**
     * 生成课程邀请码
     *
     * @param courseId  courseCode
     * @param expireDay expireDay
     * @return java.lang.String
     */
    String generateCourseInviteCode(Integer courseId, int expireDay);


    /**
     * 邀请用户加入课程
     *
     * @param param param
     */
    void inviteUsers(CourseInviteParam param);

    /**
     * 申请加入公开课
     *
     * @param courseId courseId
     */
    void applyToJoinOpenCourse(Integer courseId, Integer userId);


    /**
     * 通过邀请码加入公开课
     *
     * @param courseCode courseCode
     */
    void applyToJoinCourseByCode(String courseCode);


    /**
     * 加入课程确认
     *
     * @param param param
     */
    void checkToJoinCourse(CheckToJoinCourseParam param);

    /**
     * 获取课程详情
     *
     * @param courseId
     * @return com.olrtc.app.model.dto.CourseDto
     * @author njy
     * @since 17:35 2023/3/28
     */
    CourseDto getCourseDtoById(Integer courseId);

    /**
     * 用户修改课程
     *
     * @param courseUpdateParam
     * @return com.olrtc.app.model.dto.CourseDto
     * @author njy
     * @since 17:35 2023/3/28
     */
    CourseDto updateCourse(CourseUpdateParam courseUpdateParam);

    /**
     * 查询课程
     *
     * @param queryParam
     * @return java.util.List<com.olrtc.app.model.dto.CourseDto>
     */
    List<CourseSearchDto> queryCourse(String queryParam, Integer userId);
}
