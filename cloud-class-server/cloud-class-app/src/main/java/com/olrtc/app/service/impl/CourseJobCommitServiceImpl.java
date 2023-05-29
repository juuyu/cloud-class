package com.olrtc.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.CourseJobCommitMapper;
import com.olrtc.app.model.entity.CourseJobCommit;
import com.olrtc.app.service.CourseJobCommitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course_job_commit】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Service
public class CourseJobCommitServiceImpl extends ServiceImpl<CourseJobCommitMapper, CourseJobCommit>
        implements CourseJobCommitService {

    @Override
    public CourseJobCommit getUserCourseJobCommit(Integer courseJobId, Integer userId) {
        return getOne(new LambdaQueryWrapper<CourseJobCommit>()
                .eq(CourseJobCommit::getCourseJobId, courseJobId)
                .eq(CourseJobCommit::getStudentId, userId)
        );
    }

    @Override
    public List<CourseJobCommit> getCourseJobCommitByCourseJobId(Integer courseJobId) {
        return list(new LambdaQueryWrapper<CourseJobCommit>()
                .eq(CourseJobCommit::getCourseJobId, courseJobId)
                .orderByDesc(CourseJobCommit::getCommitTime)
        );
    }
}




