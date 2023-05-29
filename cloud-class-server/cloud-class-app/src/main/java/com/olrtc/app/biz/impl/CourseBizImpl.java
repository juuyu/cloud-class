package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olrtc.app.biz.CourseBiz;
import com.olrtc.app.config.properties.LiveServerProperties;
import com.olrtc.app.enums.CourseType;
import com.olrtc.app.enums.JoinType;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.CourseSearchDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.entity.Live;
import com.olrtc.app.model.param.CheckToJoinCourseParam;
import com.olrtc.app.model.param.CourseAddParam;
import com.olrtc.app.model.param.CourseInviteParam;
import com.olrtc.app.model.param.CourseUpdateParam;
import com.olrtc.app.random.ShortUID;
import com.olrtc.app.service.CourseService;
import com.olrtc.app.service.CourseUserService;
import com.olrtc.app.service.LiveService;
import com.olrtc.app.service.MessageService;
import com.olrtc.app.utils.CourseArrangeUtil;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.security.domain.LoginUser;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author njy
 * @since 2023/3/10 11:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseBizImpl implements CourseBiz {

    private final CourseService        courseService;
    private final CourseUserService    courseUserService;
    private final LiveServerProperties liveServerProperties;
    private final LiveService          liveService;
    private final MessageService       messageService;

    @Override
    public boolean createCourse(CourseAddParam courseAddParam) {
        Integer userId = LoginUtil.getUserId();

        List<Course> courses = courseService.getUserCreateCurses(userId);
        String weekArranged = CourseArrangeUtil.emptyWeekArrange;
        if (!courses.isEmpty()) {
            List<String> weekArranges = courses.stream()
                    .map(Course::getWeekArrange)
                    .toList();
            // 用户开设的课程每周安排合并
            weekArranged = CourseArrangeUtil.mergeUserCourseArrange(weekArranges);
        }
        boolean courseRepeat = CourseArrangeUtil.isCourseRepeat(weekArranged, courseAddParam.getWeekArrange());
        if (courseRepeat) {
            throw new BizException("课程安排重复, 无法添加");
        }
        Course course = Convert.convert(Course.class, courseAddParam);
        course.setCourseCode(UUID.randomUUID().toString().replace("-", ""));
        course.setTeacherId(userId);
        if (CourseType.PRIVATE_COURSE.equals(course.getCourseType())) {
            //私教课学生数上限1
            course.setUserNum(1);
        }
        if (CourseType.PUBLIC_COURSE.equals(course.getCourseType())) {
            //公开课课学生数无上限
            course.setUserNum(Integer.MAX_VALUE);
        }
        boolean save = courseService.save(course);
        if (save && !CourseType.PRIVATE_COURSE.equals(course.getCourseType())) {
            String roomId = UUID.randomUUID().toString().replace("-", "");
            Live live = new Live();
            live.setRoomId(roomId);
            live.setCourseId(course.getId());

            String userVideoPublish = liveServerProperties.getPublishUrl() + roomId + "-user";
            String userVideoPlay = liveServerProperties.getLiveUrl() + roomId + "-user";

            String screenVideoPublish = liveServerProperties.getPublishUrl() + roomId + "-screen";
            String screenVideoPlay = liveServerProperties.getLiveUrl() + roomId + "-screen";

            live.setUserVideoPublish(userVideoPublish);
            live.setUserVideoPlay(userVideoPlay);
            live.setScreenVideoPublish(screenVideoPublish);
            live.setScreenVideoPlay(screenVideoPlay);

            log.info(JsonUtil.obj2String(live));
            return liveService.save(live);
        }
        return save;
    }

    @Override
    public String generateCourseInviteCode(Integer courseId, int expireDay) {
        Integer userId = LoginUtil.getUserId();
        Course course = courseService.getById(courseId);
        checkCourse(course);
        if (course.getJoinEndTime().before(new Date())) {
            throw new BizException("加入该课程截止日期已过");
        }
        if (!Objects.equals(course.getTeacherId(), userId)) {
            throw new BizException("这不是你的课程");
        }
        return ShortUID.genThreeDigitSeed(course.getCourseCode(), expireDay);
    }

    @Override
    public void inviteUsers(CourseInviteParam param) {
        log.info("inviteUsers() called with parameters => [param = {}]", param);
        Integer courseId = param.getCourseId();
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BizException("该课程不存在, 邀请失败");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常, 邀请失败");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期, 邀请失败");
        }
        if (course.getJoinEndTime().before(new Date())) {
            throw new BizException("加入该课程截止日期已过, 邀请失败");
        }
        for (Integer userId : param.getUserIds()) {
            if (!Objects.equals(course.getTeacherId(), userId)) {
                CourseUser courseUser = courseUserService.getUserJoinCourseRecord(courseId, userId);
                if (courseUser == null) {
                    CourseUser courseUser1 = new CourseUser();
                    courseUser1.setCourseId(courseId);
                    courseUser1.setStudentId(userId);
                    courseUser1.setJoinType(JoinType.TEA_INVITE);
                    courseUser1.setStatus(1);
                    courseUser1.setSelectedTime(new Date());
                    boolean save = courseUserService.save(courseUser1);
                    if (save) {
                        messageService.sendInviteMessage(courseUser1);
                    }
                }
            }
        }

    }

    @Override
    public void applyToJoinOpenCourse(Integer courseId, Integer userId) {
        if (userId == null) {
            throw new BizException("请先登录");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        if (!course.getNeedOpen()) {
            throw new BizException("该课程不是公开课程, 加入失败");
        }
        joinCoursePreCheck(course, userId);
        //
        applyToJoinCourse(course, userId);
    }

    @Override
    public void applyToJoinCourseByCode(String courseCode) {
        Integer userId = LoginUtil.getUserId();
        String shortUIDValue = ShortUID.getShortUIDValue(courseCode);
        if (StrUtil.isBlank(shortUIDValue)) {
            throw new BizException("课程邀请码已过期");
        }
        Course course = courseService.getByCourseCode(shortUIDValue);
        joinCoursePreCheck(course, userId);

        applyToJoinCourse(course, userId);
    }

    @Override
    public void checkToJoinCourse(CheckToJoinCourseParam param) {
        LoginUser loginUser = LoginUtil.getLoginUser();
        if (loginUser.getUserType() == 2) {
            teaCheckToJoinCourse(param);
        } else if (loginUser.getUserType() == 3) {
            stuCheckToJoinCourse(param);
        }
    }

    @Override
    public CourseDto getCourseDtoById(Integer courseId) {
        if (courseId == null) {
            throw new BizException("课程id不能为null");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        if (userId.equals(course.getTeacherId())) {
            // 自己的课程
            return courseService.convertToDTO(course);
        }
        boolean b = courseUserService.userIsJoinCourse(userId, courseId);
        if (!b) {
            throw new BizException("你未加入此课程");
        }
        return courseService.convertToDTO(course);
    }

    @Override
    public CourseDto updateCourse(CourseUpdateParam courseUpdateParam) {
        Integer userId = LoginUtil.getUserId();
        Integer courseId = courseUpdateParam.getId();
        Course course = courseService.getById(courseId);
        if (course == null)
            throw new BizException("courseId = " + courseId + ",的课程不存在");
        if (!course.getTeacherId().equals(userId))
            throw new BizException("你不是该课程的老师, 无权修改!");

        if (!course.getWeekArrange().equals(courseUpdateParam.getWeekArrange())) {
            // 重新校验一下课程安排情况
            List<Course> courses = courseService.getUserCreateCurses(userId);
            String weekArranged = CourseArrangeUtil.emptyWeekArrange;
            if (!courses.isEmpty()) {
                List<String> weekArranges = courses.stream()
                        .filter(course1 -> !Objects.equals(course1.getId(), courseId))
                        .map(Course::getWeekArrange)
                        .toList();
                // 用户开设的课程每周安排合并
                weekArranged = CourseArrangeUtil.mergeUserCourseArrange(weekArranges);
            }
            boolean courseRepeat = CourseArrangeUtil.isCourseRepeat(weekArranged, courseUpdateParam.getWeekArrange());
            if (courseRepeat)
                throw new BizException("课程安排重复, 修改失败");
        }

//        course.setStartTime(courseUpdateParam.getStartTime());
//        course.setEndTime(courseUpdateParam.getEndTime());
//        course.setJoinEndTime(courseUpdateParam.getJoinEndTime());
        course.setCourseName(courseUpdateParam.getCourseName());
        course.setCover(courseUpdateParam.getCover());
        course.setIntro(courseUpdateParam.getIntro());
        course.setWeekArrange(courseUpdateParam.getWeekArrange());
        course.setNeedOpen(courseUpdateParam.getNeedOpen());
        course.setNeedReview(courseUpdateParam.getNeedReview());
        course.setUserNum(courseUpdateParam.getUserNum());

        boolean f = courseService.updateById(course);
        return f ? courseService.convertToDTO(courseService.getById(courseId)) : null;
    }

    @Override
    public List<CourseSearchDto> queryCourse(String queryParam, Integer userId) {
        if (userId == null) {
            throw new BizException("userId 不能为null");
        }
        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId)
                .like(StringUtils.isNotBlank(queryParam), Course::getCourseName, queryParam);
        List<Course> courses = courseService.list(lqw);
        return Convert.convert(new TypeReference<>() {
        }, courses);
    }


    private void teaCheckToJoinCourse(CheckToJoinCourseParam param) {
    }

    private void stuCheckToJoinCourse(CheckToJoinCourseParam param) {
    }


    private void applyToJoinCourse(Course course, Integer userId) {
        CourseUser courseUser = new CourseUser();
        courseUser.setCourseId(course.getId());
        courseUser.setStudentId(userId);
        //
        courseUser.setJoinType(JoinType.USER_APPLY);
        courseUser.setSelectedTime(new Date());
        if (!course.getNeedReview()) {
            // 不需要审核
            courseUser.setStatus(2);
            boolean save = courseUserService.save(courseUser);
            if (save) {
                messageService.sendAllowUserJoinMessage(courseUser);
            }
        } else {
            // 需要审核
            courseUser.setStatus(1);
            boolean save = courseUserService.save(courseUser);
            if (save) {
                messageService.sendApplyMessage(courseUser);
            }
        }
    }


    private void checkCourse(Course course) {
        if (course == null) {
            throw new BizException("该课程不存在");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期");
        }
    }

    private void joinCoursePreCheck(Course course, Integer userId) {
        if (course == null) {
            throw new BizException("该课程不存在, 加入失败");
        }
        if (course.getStatus() != 2) {
            throw new BizException("该课程状态不正常, 加入失败");
        }
        if (course.getEndTime().before(new Date())) {
            throw new BizException("该课程已过期, 加入失败");
        }
        if (course.getJoinEndTime().before(new Date())) {
            throw new BizException("加入该课程截止日期已过, 加入失败");
        }
        if (course.getUserNum() <= courseService.getJoinCourseUserNumById(course.getId())) {
            throw new BizException("该课程人数已满, 加入失败");
        }
        if (Objects.equals(course.getTeacherId(), userId)) {
            throw new BizException("你无法加入自己的课程");
        }
        CourseUser courseUser = courseUserService.getUserJoinCourseRecord(course.getId(), userId);
        if (courseUser != null) {
            if (courseUser.getStatus() == 1) {
                throw new BizException("你已经申请过加入该课程");
            }
            if (courseUser.getStatus() == 2) {
                throw new BizException("你已经加入了该课程");
            }
            if (courseUser.getStatus() == 3) {
                throw new BizException("你已被该课程拒绝,加入失败");
            }
        }
    }


}
