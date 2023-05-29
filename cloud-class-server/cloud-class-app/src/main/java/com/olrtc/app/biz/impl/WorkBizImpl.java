package com.olrtc.app.biz.impl;

import com.olrtc.app.biz.WorkBiz;
import com.olrtc.app.model.dto.CourseDayArrangeDto;
import com.olrtc.app.model.dto.UserTodayCourseDto;
import com.olrtc.app.model.dto.UserTodayCourseJobDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.service.CourseJobService;
import com.olrtc.app.service.CourseService;
import com.olrtc.app.utils.CourseArrangeUtil;
import com.olrtc.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @author njy
 * @since 2023/5/4 14:24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkBizImpl implements WorkBiz {

    private final CourseService    courseService;
    private final CourseJobService courseJobService;

    @Override
    public UserTodayCourseDto getUserTodayCourse(Integer userId) {
        if (userId == null) {
            throw new BizException("用户id不能为空");
        }
        UserTodayCourseDto userTodayCourseDto = new UserTodayCourseDto();
        List<CourseDayArrangeDto> stuCourseList = new ArrayList<>();
        List<CourseDayArrangeDto> teaCourseList = new ArrayList<>();

        int stuCourseNum = 0;
        int teaCourseNum = 0;

        List<Course> joinCourse = courseService.getJoinCourse(userId);
        if (joinCourse != null && joinCourse.size() > 0) {
            for (Course course : joinCourse) {
                String weekArrange = course.getWeekArrange();
                String todayArrange = CourseArrangeUtil.getTodayArrange(weekArrange);
                if (todayArrange != null) {
                    CourseDayArrangeDto courseDayArrangeDto = new CourseDayArrangeDto()
                            .setId(course.getId())
                            .setCourseName(course.getCourseName())
                            .setCourseCode(course.getCourseCode())
                            .setDayArrange(todayArrange);
                    stuCourseList.add(courseDayArrangeDto);
                }
            }
            stuCourseNum = stuCourseList.size();
        }


        List<Course> userCreateCurses = courseService.getUserCreateCurses(userId);
        if (userCreateCurses != null && userCreateCurses.size() > 0) {
            for (Course course : userCreateCurses) {
                String weekArrange = course.getWeekArrange();
                String todayArrange = CourseArrangeUtil.getTodayArrange(weekArrange);
                if (todayArrange != null) {
                    CourseDayArrangeDto courseDayArrangeDto = new CourseDayArrangeDto()
                            .setId(course.getId())
                            .setCourseName(course.getCourseName())
                            .setCourseCode(course.getCourseCode())
                            .setDayArrange(todayArrange);
                    teaCourseList.add(courseDayArrangeDto);
                }
            }
            teaCourseNum = teaCourseList.size();
        }


        userTodayCourseDto.setStuCourseNum(stuCourseNum)
                .setTeaCourseNum(teaCourseNum)
                .setStuCourseList(stuCourseList)
                .setTeaCourseList(teaCourseList);

        return userTodayCourseDto;
    }

    @Override
    public UserTodayCourseDto getUserTodayCourseAsync(Integer userId){
        if (userId == null) {
            throw new BizException("用户id不能为空");
        }

        CompletableFuture<List<CourseDayArrangeDto>> future1 = CompletableFuture.supplyAsync(() -> {
            List<CourseDayArrangeDto> stuCourseList = new ArrayList<>();

            List<Course> joinCourse = courseService.getJoinCourse(userId);
            if (joinCourse != null && joinCourse.size() > 0) {
                CountDownLatch countDownLatch = new CountDownLatch(joinCourse.size());
                for (Course course : joinCourse) {
                    CompletableFuture.runAsync(()->{
                        String weekArrange = course.getWeekArrange();
                        String todayArrange = CourseArrangeUtil.getTodayArrange(weekArrange);
                        if (todayArrange != null) {
                            CourseDayArrangeDto courseDayArrangeDto = new CourseDayArrangeDto()
                                    .setId(course.getId())
                                    .setCourseName(course.getCourseName())
                                    .setCourseCode(course.getCourseCode())
                                    .setDayArrange(todayArrange);
                            stuCourseList.add(courseDayArrangeDto);
                        }
                        countDownLatch.countDown();
                    });
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return stuCourseList;
        });

        CompletableFuture<List<CourseDayArrangeDto>> future2 = CompletableFuture.supplyAsync(() -> {
            List<CourseDayArrangeDto> teaCourseList = new ArrayList<>();

            List<Course> userCreateCurses = courseService.getUserCreateCurses(userId);

            if (userCreateCurses != null && userCreateCurses.size() > 0) {
                CountDownLatch countDownLatch = new CountDownLatch(userCreateCurses.size());
                for (Course course : userCreateCurses) {
                    String weekArrange = course.getWeekArrange();
                    String todayArrange = CourseArrangeUtil.getTodayArrange(weekArrange);
                    if (todayArrange != null) {
                        CourseDayArrangeDto courseDayArrangeDto = new CourseDayArrangeDto()
                                .setId(course.getId())
                                .setCourseName(course.getCourseName())
                                .setCourseCode(course.getCourseCode())
                                .setDayArrange(todayArrange);
                        teaCourseList.add(courseDayArrangeDto);
                    }
                    countDownLatch.countDown();
                }
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return teaCourseList;
        });

        CompletableFuture<UserTodayCourseDto> future = future1.thenCombine(future2, (result1, result2) -> new UserTodayCourseDto()
                .setStuCourseNum(result1.size())
                .setTeaCourseNum(result2.size())
                .setStuCourseList(result1)
                .setTeaCourseList(result2));
        try {
            return future.get();
        } catch (Exception e) {
        }
        return new UserTodayCourseDto();
    }


    @Override
    public UserTodayCourseJobDto getUserTodayCourseJob(Integer userId) {
        return null;
    }
}
