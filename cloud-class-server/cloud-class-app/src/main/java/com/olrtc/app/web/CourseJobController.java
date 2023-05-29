package com.olrtc.app.web;

import com.olrtc.app.biz.CourseJobBiz;
import com.olrtc.app.model.dto.CourseJobDto;
import com.olrtc.app.model.entity.CourseJob;
import com.olrtc.app.model.param.CourseJobAddParam;
import com.olrtc.app.model.param.CourseJobUpdateParam;
import com.olrtc.app.service.CourseJobService;
import com.olrtc.common.core.domain.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:56
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("course/job")
public class CourseJobController {

    private final CourseJobBiz     courseJobBiz;
    private final CourseJobService courseJobService;

    /**
     * 获取课程作业
     *
     * @param courseId
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.CourseJobDto>>
     * @author njy
     * @since 23:01 2023/3/14
     */
    @GetMapping
    public R<List<CourseJobDto>> getCourseJobs(Integer courseId) {
        List<CourseJobDto> jobs = courseJobBiz.getCourseJobs(courseId);
        return R.ok("查询成功", jobs);
    }


    /**
     * 获取某个课程作业信息
     *
     * @param courseJobId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseJobDto>
     * @author njy
     * @since 16:28 2023/4/6
     */
    @GetMapping("one")
    public R<CourseJobDto> getCourseJob(Integer courseJobId) {
        CourseJob courseJob = courseJobService.getById(courseJobId);
        if (courseJob == null) {
            return R.ok("ok", null);
        }
        return R.ok("ok", courseJobBiz.convert(courseJob));
    }


    /**
     * 下载作业提交结果
     *
     * @param courseJobCommitId
     * @param response
     * @author njy
     * @since 16:35 2023/4/7
     */
    @GetMapping("download/res")
    public void downloadCommitRes(Integer courseJobCommitId,
                                  HttpServletResponse response) {
        courseJobBiz.downloadCommitRes(courseJobCommitId, response);
    }

    /**
     * 发布课程作业
     *
     * @param courseJobAddParam courseJobAddParam
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 17:03 2023/4/3
     */
    @PostMapping
    public R<Void> addCourseJob(@RequestBody @Validated CourseJobAddParam courseJobAddParam) {
        log.info("addCourseJob() called with parameters => [courseJobAddParam = {}]", courseJobAddParam);
        boolean f = courseJobBiz.addCourseJob(courseJobAddParam);
        return f ? R.ok("发布成功") : R.fail("发布失败");
    }


    /**
     * 修改课程作业
     *
     * @param courseJobUpdateParam courseJobUpdateParam
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.CourseDto>
     * @author njy
     * @since 17:35 2023/3/28
     */
    @PostMapping("update")
    public R<Void> updateCourseJob(@RequestBody @Validated CourseJobUpdateParam courseJobUpdateParam) {
        log.info("updateCourseJob() called with parameters => [courseJobUpdateParam = {}]", courseJobUpdateParam);
        boolean f = courseJobBiz.updateCourseJob(courseJobUpdateParam);
        return f ? R.ok("修改成功") : R.fail("修改失败");
    }




    /**
     * 删除课程作业
     *
     * @param courseJobId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:53 2023/4/1
     */
    @GetMapping("del")
    public R<Void> delCourseJob(Integer courseJobId) {
        log.info("delCourseJob() called with parameters => [courseJobId = {}]", courseJobId);
        courseJobBiz.delCourseJob(courseJobId);
        return R.ok("删除成功");
    }


}
