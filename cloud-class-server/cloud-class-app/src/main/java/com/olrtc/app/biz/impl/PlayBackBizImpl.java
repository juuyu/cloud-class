package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.olrtc.app.biz.PlayBackBiz;
import com.olrtc.app.model.dto.PlayBackDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CoursePlayback;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.entity.Video;
import com.olrtc.app.model.param.PlayBackAddParam;
import com.olrtc.app.service.CoursePlaybackService;
import com.olrtc.app.service.CourseService;
import com.olrtc.app.service.UserService;
import com.olrtc.app.service.VideoService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author njy
 * @since 2023/3/14 22:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayBackBizImpl implements PlayBackBiz {
    private final CoursePlaybackService coursePlaybackService;
    private final CourseService         courseService;
    private final VideoService          videoService;
    private final UserService           userService;

    @Override
    public List<PlayBackDto> getPlayBacks(Integer courseId) {
        if (courseId == null) {
            throw new BizException("课程id不能为空");
        }
        List<CoursePlayback> playbacks = coursePlaybackService.list(new LambdaQueryWrapper<CoursePlayback>()
                .eq(CoursePlayback::getCourseId, courseId)
                .orderByDesc(CoursePlayback::getUploadTime)
        );
        if (CollectionUtils.isEmpty(playbacks)) {
            return new ArrayList<>();
        }
        return playbacks.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void delPlayBack(Integer id) {
        Integer userId = LoginUtil.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        CoursePlayback coursePlayback = coursePlaybackService.getById(id);
        if (coursePlayback == null) {
            throw new BizException("该课程回放不存在");
        }
        Course course = courseService.getById(coursePlayback.getCourseId());
        if (!course.getTeacherId().equals(userId)) {
            throw new BizException("你不是该课程的老师,无法删除");
        }
        coursePlaybackService.removeById(id);
    }

    @Override
    public boolean addPlayBack(PlayBackAddParam param) {
        Video video = videoService.getById(param.getVideoId());
        if (video == null) {
            throw new BizException("该视频不存在");
        }
        Integer[] courseIds = param.getCourseIds();
        if (courseIds == null || courseIds.length == 0) {
            throw new BizException("课程id不能为空");
        }

        CountDownLatch countDownLatch = new CountDownLatch(courseIds.length);
        for (Integer courseId : courseIds) {
            CompletableFuture.runAsync(() -> {
                try {
                    Course course = courseService.getById(courseId);
                    log.info("课程id:{}, course:{}", courseId, JsonUtil.obj2String(course));
                    if (course == null) {
                        log.error("课程不存在");
                        return;
                    }
                    CoursePlayback coursePlayback = new CoursePlayback();
                    coursePlayback.setCourseId(courseId);
                    coursePlayback.setPlaybackName(video.getVideoName());
                    coursePlayback.setCover(video.getVideoCoverLink());
                    coursePlayback.setUploadUserId(video.getUserId());
                    coursePlayback.setPlaybackFileLink(video.getVideoFileLink());
                    coursePlayback.setUploadTime(new Date());
                    boolean save = coursePlaybackService.save(coursePlayback);
                    log.info("添加结果:{}", save);
                } catch (Exception e) {
                    log.error("添加回放失败", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
            return true;
        } catch (InterruptedException e) {
            log.error("添加回放失败", e);
        }
        return false;
    }

    private PlayBackDto convert(CoursePlayback coursePlayback) {
        PlayBackDto playBackDto = Convert.convert(PlayBackDto.class, coursePlayback);
        playBackDto.setUploadTime(DateUtil.formatDate(coursePlayback.getUploadTime()));
        Integer uploadUserId = coursePlayback.getUploadUserId();
        if (uploadUserId != null) {
            User user = userService.getById(uploadUserId);
            if (user != null)
                playBackDto.setUploadUserName(user.getRealName());
        }
        return playBackDto;
    }


}
