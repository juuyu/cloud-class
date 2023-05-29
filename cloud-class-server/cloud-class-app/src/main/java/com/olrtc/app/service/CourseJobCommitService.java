package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.entity.CourseJobCommit;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course_job_commit】的数据库操作Service
 * @createDate 2023-02-22 10:56:10
 */
public interface CourseJobCommitService extends IService<CourseJobCommit> {

    /**
     * 获取用户提交的作业信息
     *
     * @param courseJobId
     * @param userId
     * @return com.olrtc.app.model.entity.CourseJobCommit
     * @author njy
     * @since 13:59 2023/4/6
     */
    CourseJobCommit getUserCourseJobCommit(Integer courseJobId, Integer userId);


    List<CourseJobCommit> getCourseJobCommitByCourseJobId(Integer courseJobId);

}
