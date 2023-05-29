package com.olrtc.app.service;

import com.olrtc.app.model.entity.CourseJob;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author yu
* @description 针对表【t_course_job】的数据库操作Service
* @createDate 2023-02-22 10:56:10
*/
public interface CourseJobService extends IService<CourseJob> {

    CourseJob getByJobName(String jobName, Integer courseId);
}
