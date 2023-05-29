package com.olrtc.app.biz;

import com.olrtc.app.model.dto.CourseFileDto;
import com.olrtc.app.model.param.CourseFileAddParam;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:42
 */
public interface CourseFileBiz {
    /**
     * 获取课程的课程资料信息
     *
     * @param courseId
     * @return java.util.List<com.olrtc.app.model.dto.CourseFileDto>
     * @author njy
     * @since 23:02 2023/4/1
     */
    List<CourseFileDto> getCourseFiles(Integer courseId);

    /**
     * 上传课程资料
     *
     * @param courseFileAddParam
     * @return boolean
     * @author njy
     * @since 23:03 2023/4/1
     */
    boolean addCourseFile(CourseFileAddParam courseFileAddParam);

    /**
     * 删除课程资料
     *
     * @param courseFileId
     * @return void
     * @author njy
     * @since 23:03 2023/4/1
     */
    void delCourseFile(Integer courseFileId);
}
