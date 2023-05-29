package com.olrtc.app.web;

import com.olrtc.app.biz.CourseBiz;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.CourseSearchDto;
import com.olrtc.app.model.param.*;
import com.olrtc.app.service.CourseService;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author njy
 * @since 2023/2/27 23:11
 */
@Slf4j
@RestController
@RequestMapping("course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseBiz     courseBiz;

    /**
     * 查询用户加入的课程信息
     *
     * @param pageQuery pageQuery
     * @param param     param
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.CourseDto>>
     * @author njy
     * @since 10:00 2023/2/28
     */
    @GetMapping
    public R<TableDataInfo<CourseDto>> listJoinCourse(PageQuery pageQuery, CourseQueryParam param) {
        log.info("listJoinCourse() called with parameters => [pageQuery = {}], [param = {}]", pageQuery, param);
        TableDataInfo<CourseDto> courseList = courseService.getJoinCourseList(pageQuery, param);
        return R.ok("查询成功", courseList);
    }

    /**
     * 查询用户创建的课程信息
     *
     * @param pageQuery pageQuery
     * @param param     param
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.CourseDto>>
     * @author njy
     * @since 09:56 2023/3/24
     */
    @GetMapping("created")
    public R<TableDataInfo<CourseDto>> listCreatedCourse(PageQuery pageQuery, CourseQueryParam param) {
        TableDataInfo<CourseDto> courseList = courseService.getCreatedCourseList(pageQuery, param);
        return R.ok("查询成功", courseList);
    }


    /**
     * 获取课程详情
     *
     * @param courseId courseId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseDto>
     * @author njy
     * @since 22:16 2023/3/14
     */
    @GetMapping("info")
    public R<CourseDto> getCourseByCourseId(Integer courseId) {
        CourseDto courseDto = courseBiz.getCourseDtoById(courseId);
        return R.ok("查询成功", courseDto);
    }

    /**
     * 获取课程
     *
     * @param queryParam queryParam
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.UserDto>>
     */
    @GetMapping("query")
    public R<List<CourseSearchDto>> queryCourse(String queryParam, Integer userId) {
        List<CourseSearchDto> courses = courseBiz.queryCourse(queryParam, userId);
        return R.ok("ok", courses);
    }

    /**
     * 用户开设课程
     *
     * @param courseAddParam courseAddParam
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseDto>
     * @author njy
     * @since 19:28 2023/2/28
     */
    @PostMapping
    public R<Void> addCourse(@RequestBody @Validated CourseAddParam courseAddParam) {
        log.info("addCourse() called with parameters => [courseAddParam = {}]", courseAddParam);
        boolean course = courseBiz.createCourse(courseAddParam);
        return course ? R.ok("开课成功") : R.fail("开课失败");
    }


    /**
     * 用户修改课程
     *
     * @param courseUpdateParam
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseDto>
     * @author njy
     * @since 17:35 2023/3/28
     */
    @PostMapping("update")
    public R<CourseDto> updateCourse(@RequestBody @Validated CourseUpdateParam courseUpdateParam) {
        log.info("updateCourse() called with parameters => [courseUpdateParam = {}]", courseUpdateParam);
        CourseDto courseDto = courseBiz.updateCourse(courseUpdateParam);
        return courseDto != null ? R.ok("修改成功", courseDto) : R.fail("修改失败");
    }


    /**
     * generate course invite code
     *
     * @param courseId  courseId
     * @param expireDay expireDay
     * @return com.olrtc.common.core.domain.R<java.lang.String>
     */
    @GetMapping("createCode")
    public R<String> generateCourseInviteCode(Integer courseId, int expireDay) {
        log.info("generateCourseInviteCode() called with parameters => [courseId = {}], [expireDay = {}]", courseId, expireDay);
        if (expireDay > 5) {
            expireDay = 5;
        }
        if (expireDay < 1) {
            expireDay = 1;
        }
        String code = courseBiz.generateCourseInviteCode(courseId, expireDay);
        return R.ok("生成成功", code);
    }


    /**
     * 邀请用户加入自己的课程
     *
     * @param param param
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @PostMapping("invite")
    public R<Void> inviteUserToCourse(@RequestBody @Validated CourseInviteParam param) {
        courseBiz.inviteUsers(param);
        return R.ok("邀请成功");
    }


    /**
     * 申请加入公开课
     *
     * @param courseId courseId
     * @param userId   userId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @GetMapping("apply")
    public R<Void> applyToJoinOpenCourse(Integer courseId, Integer userId) {
        courseBiz.applyToJoinOpenCourse(courseId, userId);
        return R.ok("申请成功");
    }


    /**
     * 申请加入课程（邀请码）
     *
     * @param courseCode courseCode
     */
    @GetMapping("apply/code")
    public R<Void> applyToJoinCourseByCode(String courseCode) {
        courseBiz.applyToJoinCourseByCode(courseCode);
        return R.ok("申请成功");
    }


    /**
     * 确认加入课程(废弃)
     *
     * @param param param
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 13:23 2023/3/10
     */
    @Deprecated
    @GetMapping("check")
    public R<Void> checkToJoinCourse(@Validated CheckToJoinCourseParam param) {
        courseBiz.checkToJoinCourse(param);
        return R.ok();
    }


}
