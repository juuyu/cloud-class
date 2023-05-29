package com.olrtc.app.web;

import com.olrtc.app.biz.CourseUserBiz;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:17
 */
@Slf4j
@RestController
@RequestMapping("course/user")
@RequiredArgsConstructor
public class CourseUserController {

    private final CourseUserBiz courseUserBiz;

    /**
     * 获取加入课程的人员信息
     *
     * @param pageQuery
     * @param courseId
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.UserDto>>
     * @author njy
     * @since 22:41 2023/3/14
     */
    @GetMapping
    public R<TableDataInfo<UserDto>> listJoinCourseUsers(PageQuery pageQuery, Integer courseId) {
        TableDataInfo<UserDto> userList = courseUserBiz.listJoinCourseUsers(pageQuery, courseId);
        return R.ok("查询成功", userList);
    }

    /**
     * 获取加入课程的人员信息（不分页）
     *
     * @param courseId
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.UserDto>>
     * @author njy
     * @since 11:12 2023/4/10
     */
    @GetMapping("list")
    public R<List<UserDto>> listJoinCourseUserList(Integer courseId) {
        List<UserDto> userList = courseUserBiz.listJoinCourseUserList(courseId);
        return R.ok("查询成功", userList);
    }


    /**
     * 踢人
     *
     * @param courseId
     * @param userId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 13:21 2023/3/29
     */
    @GetMapping("kick")
    public R<Void> kickUserInCourse(Integer courseId, Integer userId) {
        courseUserBiz.kickUserInCourse(courseId, userId);
        return R.ok("删除成功");
    }



    /**
     * 退出课程
     *
     * @param courseId
     * @param userId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @since 13:21 2023/3/29
     */
    @GetMapping("exit")
    public R<Void> exitCourse(Integer courseId, Integer userId) {
        courseUserBiz.exitCourse(courseId, userId);
        return R.ok("退出成功");
    }


}
