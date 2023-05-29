package com.olrtc.app.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olrtc.app.biz.UserTableBiz;
import com.olrtc.app.model.domain.PieChart;
import com.olrtc.app.model.dto.UserCourseTableDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.param.UserCourseTableQueryParam;
import com.olrtc.app.service.CourseService;
import com.olrtc.app.service.CourseUserService;
import com.olrtc.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author njy
 * @since 2023/4/18 09:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTableBizImpl implements UserTableBiz {

    private final CourseService     courseService;
    private final CourseUserService courseUserService;

    @Override
    public UserCourseTableDto getUserCourseTable(UserCourseTableQueryParam param) {
        Integer userId = param.getUserId();
        if (userId == null) {
            throw new BizException("userId不能为null");
        }
        Date date = new Date();
        //一天的开始，结果：2017-03-01 00:00:00
        Date beginOfDay = DateUtil.beginOfDay(date);
        //一天的结束，结果：2017-03-01 23:59:59
        Date endOfDay = DateUtil.endOfDay(date);
        List<UserCourseTableDto.TodayTodo> todayTodos = new CopyOnWriteArrayList<>();

        CompletableFuture.supplyAsync(() -> {
            List<Course> joinCourse = courseService.getJoinCourse(userId);
            if (joinCourse != null && !CollectionUtils.isEmpty(joinCourse)) {
                joinCourse.forEach(course -> {
                    String weekArrange = course.getWeekArrange();
//                    String todayArrange = CourseArrangeUtil.getTodayArrange(weekArrange);

                    todayTodos.add(new UserCourseTableDto.TodayTodo());
                });

            }

            return null;
        });


        List<Course> userCreateCurses = courseService.getUserCreateCurses(userId);
        if (userCreateCurses != null && !CollectionUtils.isEmpty(userCreateCurses)) {

        }

        return null;
    }

    @Override
    public List<PieChart> getJoinCourseInfo(Integer userId) {
        if (userId == null) {
            throw new BizException("userId不能为null");
        }
        List<PieChart> list = new CopyOnWriteArrayList<>();
        CompletableFuture<Long> getJoinNumFuture = CompletableFuture.supplyAsync(() -> {
            // 获取全部用户加入/加入ing的课程关系
            List<CourseUser> courseUsers = courseUserService.getCourseUserByUserId(userId);
            if (courseUsers == null || courseUsers.isEmpty()) {
                list.add(new PieChart(0L, "申请中"));
                return null;
            }
            // 已加入
            long count = courseUsers.stream().filter(r -> r.getStatus() == 2).count();
            long applyNum = courseUsers.size() - count;
            list.add(new PieChart(applyNum, "申请中"));
            return count;
        });

        //加入的进行中的课程
        CompletableFuture<Long> getJoinCourseFuture = CompletableFuture.supplyAsync(() -> {
            List<Course> joinCourse = courseService.getJoinCourse(userId);
            if (joinCourse == null || joinCourse.isEmpty()) {
                list.add(new PieChart(0L, "进行中"));
                return 0L;
            }
            long size = joinCourse.size();
            list.add(new PieChart(size, "进行中"));
            return size;
        });

        CompletableFuture<List<PieChart>> future = getJoinNumFuture.thenCombine(getJoinCourseFuture, (result1, result2) -> {
            if (result1 == null) {
                list.add(new PieChart(0L, "已完结"));
            } else {
                list.add(new PieChart(result1 - result2, "已完结"));
            }
            return list;
        });
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("网络异常");
        }
    }

    @Override
    public List<PieChart> getCreateCourseInfo(Integer userId) {
        if (userId == null) {
            throw new BizException("userId不能为null");
        }
        List<PieChart> list = new CopyOnWriteArrayList<>();
        CompletableFuture<Long> getCreateNumFuture = CompletableFuture.supplyAsync(() -> {
            List<Course> courses = courseService.list(new LambdaQueryWrapper<Course>()
                    .eq(Course::getTeacherId, userId));
            if (courses == null || courses.isEmpty()) {
                return null;
            }
            return (long) courses.size();
        });

        CompletableFuture<Long> getCreateCourseFuture = CompletableFuture.supplyAsync(() -> {
            List<Course> userCreateCurses = courseService.getUserCreateCurses(userId);
            if (userCreateCurses == null || userCreateCurses.isEmpty()) {
                list.add(new PieChart(0L, "进行中"));
                return 0L;
            }
            long size = userCreateCurses.size();
            list.add(new PieChart(size, "进行中"));
            return size;
        });

        CompletableFuture<List<PieChart>> future = getCreateNumFuture.thenCombine(getCreateCourseFuture, (result1, result2) -> {
            if (result1 == null) {
                list.add(new PieChart(0L, "已完结"));
            } else {
                list.add(new PieChart(result1 - result2, "已完结"));
            }
            return list;
        });
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("网络异常");
        }
    }

}
