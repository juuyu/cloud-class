package com.olrtc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.CourseMapper;
import com.olrtc.app.dao.CourseUserMapper;
import com.olrtc.app.enums.JoinType;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.service.CourseUserService;
import com.olrtc.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course_user】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseUserServiceImpl extends ServiceImpl<CourseUserMapper, CourseUser>
        implements CourseUserService {

    private final MessageService messageService;
    private final CourseMapper   courseMapper;

    @Override
    public List<CourseUser> getCourseUserByUserId(Integer userId) {
        return list(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getStudentId, userId)
        );
    }

    @Override
    public CourseUser getUserJoinCourseRecord(Integer courseId, Integer userId) {
        return getOne(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getStudentId, userId)
        );
    }

    @Override
    public List<CourseUser> getCourseUsersByCourseId(Integer courseId) {
        return list(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getCourseId, courseId)
        );
    }

    @Override
    public boolean userIsJoinCourse(Integer userId, Integer courseId) {
        CourseUser courseUser = getOne(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getStudentId, userId)
        );
        return courseUser != null;
    }

    @Override
    public boolean stuCheckToJoinCourse(CourseUser courseUser, boolean checkRes) {
        log.info("stuCheckToJoinCourse() called with parameters => [courseUser = {}], [checkRes = {}]", courseUser, checkRes);
        if (courseUser != null) {
            if (JoinType.TEA_INVITE.equals(courseUser.getJoinType())) {
                if (courseUser.getStatus() == 1) {
                    if (checkRes) {
                        courseUser.setStatus(2);
                        boolean f = updateById(courseUser);
                        if (f) {
                            messageService.sendUserJoinMessage(courseUser);
                            return true;
                        }
                    } else {
                        boolean f = removeById(courseUser);
                        if (f) {
                            messageService.sendUserRefuseMessage(courseUser);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean teaCheckToJoinCourse(CourseUser courseUser, boolean checkRes) {
        log.info("teaCheckToJoinCourse() called with parameters => [courseUser = {}], [checkRes = {}]", courseUser, checkRes);
        if (courseUser == null) {
            log.warn("courseUser is null");
            return false;
        }
        if (JoinType.USER_APPLY.equals(courseUser.getJoinType())) {
            if (courseUser.getStatus() == 1) {
                if (checkRes) {
                    courseUser.setStatus(2);
                    boolean f = updateById(courseUser);
                    if (f) {
                        messageService.sendAllowUserJoinMessage(courseUser);
                        return true;
                    }
                } else {
                    boolean f = removeById(courseUser);
                    if (f) {
                        messageService.sendRefuseUserJoinMessage(courseUser);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}




