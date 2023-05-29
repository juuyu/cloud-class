package com.olrtc.app.web;

import com.olrtc.app.biz.CourseFileBiz;
import com.olrtc.app.model.dto.CourseFileDto;
import com.olrtc.app.model.param.CourseAddParam;
import com.olrtc.app.model.param.CourseFileAddParam;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:42
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("course/file")
public class CourseFileController {

    private final CourseFileBiz courseFileBiz;

    /**
     * 获取课程文件
     *
     * @param courseId courseId
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.CourseFileDto>>
     * @author njy
     * @since 22:46 2023/3/14
     */
    @GetMapping
    public R<List<CourseFileDto>> getCourseFiles(Integer courseId) {
        List<CourseFileDto> files = courseFileBiz.getCourseFiles(courseId);
        return R.ok("查询成功", files);
    }


    /**
     * 上传课程资料
     *
     * @param courseFileAddParam courseFileAddParam
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:49 2023/4/1
     */
    @PostMapping
    public R<Void> addCourseFile(@RequestBody @Validated CourseFileAddParam courseFileAddParam) {
        log.info("addCourseFile() called with parameters => [courseFileAddParam = {}]", courseFileAddParam);
        boolean course = courseFileBiz.addCourseFile(courseFileAddParam);
        return course ? R.ok("上传成功") : R.fail("上传失败");
    }


    /**
     * 删除课程资料
     *
     * @param courseFileId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:53 2023/4/1
     */
    @GetMapping("del")
    public R<Void> delCourseFile(Integer courseFileId) {
        log.info("delCourseFile() called with parameters => [courseFileId = {}]",courseFileId);
        courseFileBiz.delCourseFile(courseFileId);
        return R.ok("删除成功");
    }


}
