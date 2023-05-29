package com.olrtc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.CourseJobMapper;
import com.olrtc.app.model.entity.CourseJob;
import com.olrtc.app.service.CourseJobService;
import org.springframework.stereotype.Service;

/**
 * @author yu
 * @description 针对表【t_course_job】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Service
public class CourseJobServiceImpl extends ServiceImpl<CourseJobMapper, CourseJob>
        implements CourseJobService {

    @Override
    public CourseJob getByJobName(String jobName, Integer courseId) {
        return getOne(new LambdaQueryWrapper<CourseJob>()
                .eq(CourseJob::getCourseId, courseId)
                .eq(CourseJob::getJobName, jobName));
    }
}




