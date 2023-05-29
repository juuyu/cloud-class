package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olrtc.app.biz.CourseUserBiz;
import com.olrtc.app.dao.UserMapper;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.service.CourseService;
import com.olrtc.app.service.CourseUserService;
import com.olrtc.app.service.MessageService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseUserBizImpl implements CourseUserBiz {

    private final CourseUserService courseUserService;
    private final CourseService     courseService;
    private final UserMapper        userMapper;
    private final MessageService    messageService;

    @Override
    public TableDataInfo<UserDto> listJoinCourseUsers(PageQuery pageQuery, Integer courseId) {
        List<Integer> joinCourseUsers = getJoinCourseUsers(courseId);
        if (joinCourseUsers.isEmpty()) {
            return TableDataInfo.build(new ArrayList<>());
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>()
                .in(User::getId, joinCourseUsers);

        Page<User> users = userMapper.selectPage(pageQuery.build(), lqw);
        IPage<UserDto> userDtoIPage = users.convert(this::convertToDTO);

        return TableDataInfo.build(userDtoIPage);
    }

    @Override
    public List<UserDto> listJoinCourseUserList(Integer courseId) {
        List<Integer> joinCourseUsers = getJoinCourseUsers(courseId);
        if (joinCourseUsers.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>()
                .in(User::getId, joinCourseUsers);
        List<User> users = userMapper.selectList(lqw);
        return Convert.convert(new TypeReference<>() {
        }, users);
    }

    @Override
    public void kickUserInCourse(Integer courseId, Integer userId) {
        Integer teaId = LoginUtil.getUserId();
        Course course = courseService.getById(courseId);
        if (!course.getTeacherId().equals(teaId)) {
            throw new BizException("你不是该课程的老师,无法删除");
        }
        CourseUser courseUser = courseUserService.getUserJoinCourseRecord(courseId, userId);
        if (courseUser != null) {
            if (courseUser.getStatus() == 2) {
                boolean f = courseUserService.removeById(courseUser);
                if (f) {
                    messageService.sendKickUserMessage(courseUser);
                }
            }
        }
    }

    @Override
    public void exitCourse(Integer courseId, Integer userId) {
        CourseUser courseUser = courseUserService.getUserJoinCourseRecord(courseId, userId);
        if (courseUser != null) {
            if (courseUser.getStatus() == 2) {
                courseUserService.removeById(courseUser);
            }
        }
    }


    private List<Integer> getJoinCourseUsers(Integer courseId) {
        List<CourseUser> courseUsers = courseUserService.getCourseUsersByCourseId(courseId);
        if (CollectionUtils.isEmpty(courseUsers)) {
            return new ArrayList<>();
        }
        return courseUsers.stream()
                .filter(courseUser -> courseUser.getStatus() == 2)
                .map(CourseUser::getStudentId)
                .toList();
    }

    private UserDto convertToDTO(User user) {
        return Convert.convert(UserDto.class, user);
    }
}
