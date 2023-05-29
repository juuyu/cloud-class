package com.olrtc.app.biz;

import com.olrtc.app.model.dto.CourseJobCommitDto;
import com.olrtc.app.model.dto.CourseJobDto;
import com.olrtc.app.model.dto.UserJobCommitDto;
import com.olrtc.app.model.dto.UserJobCommitInfoDto;
import com.olrtc.app.model.entity.CourseJob;
import com.olrtc.app.model.entity.CourseJobCommit;
import com.olrtc.app.model.param.CourseJobAddParam;
import com.olrtc.app.model.param.CourseJobCheckParam;
import com.olrtc.app.model.param.CourseJobCommitParam;
import com.olrtc.app.model.param.CourseJobUpdateParam;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:56
 */
public interface CourseJobBiz {

    /**
     * @param courseId
     * @return java.util.List<com.olrtc.app.model.dto.CourseJobDto>
     * @author njy
     * @since 17:06 2023/4/3
     */
    List<CourseJobDto> getCourseJobs(Integer courseId);

    /**
     * @param courseJobAddParam
     * @return boolean
     * @author njy
     * @since 17:07 2023/4/3
     */
    boolean addCourseJob(CourseJobAddParam courseJobAddParam);

    /**
     * @param courseJobCommitParam
     * @return boolean
     * @author njy
     * @since 17:07 2023/4/3
     */
    boolean submit(CourseJobCommitParam courseJobCommitParam);

    /**
     * 删除课程作业
     *
     * @param courseJobId
     * @return void
     * @author njy
     * @since 11:07 2023/4/4
     */
    void delCourseJob(Integer courseJobId);

    /**
     * 修改课程作业
     *
     * @param courseJobUpdateParam
     * @return boolean
     * @author njy
     * @since 11:45 2023/4/4
     */
    boolean updateCourseJob(CourseJobUpdateParam courseJobUpdateParam);

    /**
     * 获取用户作业提交信息
     *
     * @param courseJobId
     * @param userId
     * @return com.olrtc.app.model.dto.CourseJobCommitDto
     * @author njy
     * @since 13:57 2023/4/6
     */
    CourseJobCommitDto getUserCommitJob(Integer courseJobId, Integer userId);


    /**
     * convert
     *
     * @param courseJob
     * @return com.olrtc.app.model.dto.CourseJobDto
     * @author njy
     * @since 23:21 2023/4/8
     */
    CourseJobDto convert(CourseJob courseJob);


    /**
     * convert
     *
     * @param courseJobCommit
     * @return com.olrtc.app.model.dto.CourseJobCommitDto
     * @author njy
     * @since 23:21 2023/4/8
     */
    CourseJobCommitDto convertCourseJobCommit(CourseJobCommit courseJobCommit);


    /**
     * downloadCommitRes
     *
     * @param courseJobCommitId
     * @param response
     * @return void
     * @author njy
     * @since 23:21 2023/4/8
     */
    void downloadCommitRes(Integer courseJobCommitId, HttpServletResponse response);

    UserJobCommitInfoDto getCommitNumInfo(Integer courseJobId);

    List<UserJobCommitDto> getUserCommitInfoList(Integer courseJobId);

    void downloadCourseJobCommits(Integer courseJobId, HttpServletResponse response);

    boolean check(CourseJobCheckParam courseJobCheckParam);
}
