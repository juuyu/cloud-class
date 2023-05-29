package com.olrtc.app.web;

import com.olrtc.app.biz.CourseJobBiz;
import com.olrtc.app.model.dto.CourseJobCommitDto;
import com.olrtc.app.model.dto.UserJobCommitDto;
import com.olrtc.app.model.dto.UserJobCommitInfoDto;
import com.olrtc.app.model.param.CourseJobCheckParam;
import com.olrtc.app.model.param.CourseJobCommitParam;
import com.olrtc.common.core.domain.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author njy
 * @since 2023/4/8 23:44
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("course/job/commit")
public class CourseJobCommitController {

    private final CourseJobBiz courseJobBiz;

    /**
     * 获取用户作业提交信息
     *
     * @param courseJobId courseJobId
     * @param userId      userId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseJobCommitDto>
     * @author njy
     * @since 14:48 2023/4/6
     */
    @GetMapping("info")
    public R<CourseJobCommitDto> getUserCommitInfo(Integer courseJobId, Integer userId) {
        CourseJobCommitDto courseJobCommitDto = courseJobBiz.getUserCommitJob(courseJobId, userId);
        return R.ok("ok", courseJobCommitDto);
    }


    /**
     * 获取全部用户作业提交信息
     *
     * @param courseJobId
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.UserJobCommitDto>>
     * @author njy
     * @since 02:23 2023/4/9
     */
    @GetMapping("list")
    public R<List<UserJobCommitDto>> getUserCommitInfoList(Integer courseJobId) {
        log.info("getUserCommitInfoList() called with parameters => [courseJobId = {}]", courseJobId);
        List<UserJobCommitDto> courseJobCommitList = courseJobBiz.getUserCommitInfoList(courseJobId);
        return R.ok("查询成功", courseJobCommitList);
    }


    /**
     * 下载全部作业提交结果
     *
     * @param courseJobId
     * @param response
     * @author njy
     * @since 16:35 2023/4/7
     */
    @GetMapping("download/all")
    public void downloadCourseJobCommits(Integer courseJobId,
                                         HttpServletResponse response) {
        courseJobBiz.downloadCourseJobCommits(courseJobId, response);
    }


    /**
     * 获取提交数量信息
     *
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserJobCommitInfoDto>
     * @author njy
     * @since 23:49 2023/4/8
     */
    @GetMapping("num")
    public R<UserJobCommitInfoDto> getCommitNumInfo(Integer courseJobId) {
        UserJobCommitInfoDto userJobCommitInfoDto = courseJobBiz.getCommitNumInfo(courseJobId);
        return R.ok("ok", userJobCommitInfoDto);
    }


    /**
     * 提交课程作业
     *
     * @param courseJobCommitParam courseJobAddParam
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 17:03 2023/4/3
     */
    @PostMapping()
    public R<Void> addCourseJob(@RequestBody @Validated CourseJobCommitParam courseJobCommitParam) {
        log.info("addCourseJob() called with parameters => [courseJobSubmitParam = {}]", courseJobCommitParam);
        boolean f = courseJobBiz.submit(courseJobCommitParam);
        return f ? R.ok("提交成功") : R.fail("提交失败");
    }


    /**
     * 批改作业
     *
     * @param courseJobCheckParam courseJobCheckParam
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 21:40 2023/4/9
     */
    @PostMapping("check")
    public R<Void> checkJobCommit(@RequestBody @Validated CourseJobCheckParam courseJobCheckParam) {
        log.info("checkJobCommit() called with parameters => [courseJobCheckParam = {}]", courseJobCheckParam);
        boolean f = courseJobBiz.check(courseJobCheckParam);
        return f ? R.ok("批改成功") : R.fail("批改失败");
    }
}
