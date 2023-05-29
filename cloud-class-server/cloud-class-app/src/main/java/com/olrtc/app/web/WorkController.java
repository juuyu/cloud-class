package com.olrtc.app.web;

import com.olrtc.app.biz.WorkBiz;
import com.olrtc.app.model.dto.UserTodayCourseDto;
import com.olrtc.app.model.dto.UserTodayCourseJobDto;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/5/4 14:23
 */
@Slf4j
@RestController
@RequestMapping("work")
@RequiredArgsConstructor
public class WorkController {
    private final WorkBiz workBiz;

    /**
     * 获取用户今日课程
     *
     * @param userId userId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserTodayCourseDto>
     * @author njy
     * @since 14:27 2023/5/4
     */
    @GetMapping("userTodayCourse")
    public R<UserTodayCourseDto> getUserTodayCourse(Integer userId) {
        UserTodayCourseDto userTodayCourseDto = workBiz.getUserTodayCourse(userId);
        return R.ok("获取用户今日课程成功", userTodayCourseDto);
    }

    /**
     * 获取用户今日作业
     *
     * @param userId userId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserTodayCourseJobDto>
     * @author njy
     * @since 14:27 2023/5/4
     */
    @GetMapping("userTodayCourseJob")
    public R<UserTodayCourseJobDto> getUserTodayCourseJob(Integer userId) {
        UserTodayCourseJobDto userTodayCourseJobDto = workBiz.getUserTodayCourseJob(userId);
        return R.ok("获取用户今日作业成功", userTodayCourseJobDto);

    }

}
