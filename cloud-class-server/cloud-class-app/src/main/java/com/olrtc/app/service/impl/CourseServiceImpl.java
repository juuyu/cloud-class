package com.olrtc.app.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.CourseMapper;
import com.olrtc.app.dao.CourseUserMapper;
import com.olrtc.app.dao.UserMapper;
import com.olrtc.app.enums.CourseStatus;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.param.CourseQueryParam;
import com.olrtc.app.service.CourseService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {


    private final CourseMapper     courseMapper;
    private final UserMapper       userMapper;
    private final CourseUserMapper courseUserMapper;

    private List<Integer> getJoinCourseIds(Integer userId) {
        List<CourseUser> courseUsers = courseUserMapper.selectList(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getStudentId, userId)
                .eq(CourseUser::getStatus, 2));
        if (CollectionUtils.isEmpty(courseUsers)) {
            return null;
        }
        return courseUsers.stream()
                .map(CourseUser::getCourseId)
                .toList();
    }

    @Override
    public List<Course> getJoinCourse(Integer userId) {
        List<Integer> joinCourseIds = getJoinCourseIds(userId);
        if (joinCourseIds == null) {
            return null;
        }
        return courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .in(Course::getId, joinCourseIds)
                .ge(Course::getEndTime, new Date())
        );
    }


    @Override
    public TableDataInfo<CourseDto> getJoinCourseList(PageQuery pageQuery, CourseQueryParam param) {
        Integer userId = LoginUtil.getUserId();
//        if (userId == null) {
//            throw new ServiceException("当前登录用户信息已过期, 请重新登录");
//        }
        List<Integer> joinCourseIds = getJoinCourseIds(userId);
        if (joinCourseIds == null) {
            return null;
        }

        Date normal = null;
        Date expire = null;
        if (param.getCourseStatus() != null) {
            if (CourseStatus.EXPIRE.equals(param.getCourseStatus()))
                expire = new Date();
            if (CourseStatus.NORMAL.equals(param.getCourseStatus()))
                normal = new Date();
        }

        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<Course>()
                .in(Course::getId, joinCourseIds)
                .like(StrUtil.isNotBlank(param.getCourseName()), Course::getCourseName, param.getCourseName())
                .eq(param.getCourseType() != null, Course::getCourseType, param.getCourseType())
                .ge(normal != null, Course::getEndTime, normal)
                .le(expire != null, Course::getEndTime, expire);

//        IPage<CourseDto> courseDtoIPage = courseMapper.selectDtoPage(pageQuery.build(), lqw);

        Page<Course> coursePage = courseMapper.selectPage(pageQuery.build(), lqw);
        IPage<CourseDto> courseDtoIPage = coursePage.convert(this::convertToDTO);
        return TableDataInfo.build(courseDtoIPage);
    }

    @Override
    public CourseDto convertToDTO(Course course) {
        CourseDto courseDto = new CourseDto()
                .setId(course.getId())
                .setCourseName(course.getCourseName())
                .setCourseCode(course.getCourseCode())
                .setIntro(course.getIntro())
                .setCourseType(course.getCourseType())
                .setCover(course.getCover())
                .setTeacherId(course.getTeacherId())
                .setNeedOpen(course.getNeedOpen())
                .setNeedReview(course.getNeedReview());


        User user = userMapper.selectById(course.getTeacherId());
        if (user == null) {
            throw new BizException("该课程教师信息不存在");
        }
        courseDto.setTeacherName(user.getRealName());

        Date startTime = course.getStartTime();
        Date endTime = course.getEndTime();
        Date joinEndTime = course.getJoinEndTime();
        Date date = new Date();
        if (date.before(endTime)) {
            courseDto.setCourseStatus(CourseStatus.NORMAL);
        } else {
            courseDto.setCourseStatus(CourseStatus.EXPIRE);
        }

        courseDto.setStartTime(DateUtil.formatDate(startTime));
        courseDto.setEndTime(DateUtil.formatDate(endTime));
        courseDto.setJoinEndTime(DateUtil.formatDate(joinEndTime));

        courseDto.setWeekArrange(course.getWeekArrange());
        courseDto.setUserNum(course.getUserNum());

        List<CourseUser> courseUserList = courseUserMapper.selectList(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getCourseId, course.getId())
                .eq(CourseUser::getStatus, 2)
        );
        courseDto.setCourseUserNum(courseUserList.size());

        return courseDto;
    }

    @Override
    public Course getByCourseCode(String courseCode) {
        return courseMapper.selectOne(new LambdaQueryWrapper<Course>()
                .eq(Course::getCourseCode, courseCode));
    }


    @Override
    public int getJoinCourseUserNumById(Integer courseId) {
        List<CourseUser> courseUsers = courseUserMapper.selectList(new LambdaQueryWrapper<CourseUser>()
                .eq(CourseUser::getCourseId, courseId)
                .eq(CourseUser::getStatus, 2)
        );
        return courseUsers != null ? courseUsers.size() : 0;
    }

    @Override
    public List<Course> getUserCreateCurses(Integer userId) {
        // 用户已经开设的课程[teaId == userId, endTime >= now]
        return courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId)
                .ge(Course::getEndTime, new Date())
        );
    }

    @Override
    public TableDataInfo<CourseDto> getCreatedCourseList(PageQuery pageQuery, CourseQueryParam param) {
        Integer userId = LoginUtil.getUserId();

        Date normal = null;
        Date expire = null;
        if (param.getCourseStatus() != null) {
            if (CourseStatus.EXPIRE.equals(param.getCourseStatus()))
                expire = new Date();
            if (CourseStatus.NORMAL.equals(param.getCourseStatus()))
                normal = new Date();
        }

        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, userId)
                .like(StrUtil.isNotBlank(param.getCourseName()), Course::getCourseName, param.getCourseName())
                .eq(param.getCourseType() != null, Course::getCourseType, param.getCourseType())
                .ge(normal != null, Course::getEndTime, normal)
                .le(expire != null, Course::getEndTime, expire);

        Page<Course> coursePage = courseMapper.selectPage(pageQuery.build(), lqw);
        IPage<CourseDto> courseDtoIPage = coursePage.convert(this::convertToDTO);
        return TableDataInfo.build(courseDtoIPage);
    }

    @Override
    public TableDataInfo<CourseDto> getOpenCourseList(PageQuery pageQuery, CourseQueryParam param) {
        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<Course>()
                .eq(Course::getNeedOpen, true)
                .eq(param.getCourseType() != null, Course::getCourseType, param.getCourseType())
                .like(StrUtil.isNotBlank(param.getCourseName()), Course::getCourseName, param.getCourseName())
                .ge(Course::getEndTime, new Date());

        Page<Course> coursePage = courseMapper.selectPage(pageQuery.build(), lqw);
        IPage<CourseDto> courseDtoIPage = coursePage.convert(this::convertToDTO);
        return TableDataInfo.build(courseDtoIPage);
    }

    @Override
    public List<CourseDto> getHotOpenCourse() {
        LambdaQueryWrapper<Course> lqw = new LambdaQueryWrapper<Course>()
                .eq(Course::getNeedOpen, true)
                .ge(Course::getEndTime, new Date());
        // 开放的课程信息
        List<Course> courses = courseMapper.selectList(lqw);
        if (courses == null || courses.size() == 0) {
            return new ArrayList<>();
        }
        List<CourseDto> courseDtos = courses.stream().parallel()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(CourseDto::getCourseUserNum).reversed())
                .toList();
        if (courseDtos.size() > 4) {
            courseDtos = courseDtos.subList(0, 4);
        }
        return courseDtos;
    }

}




