package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.entity.Course;
import com.olrtc.app.model.param.CourseQueryParam;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_course】的数据库操作Service
 * @createDate 2023-02-22 10:56:10
 */
public interface CourseService extends IService<Course> {


    List<Course> getJoinCourse(Integer userId);

    /**
     * 分页查询用户加入的课程信息
     *
     * @param pageQuery pageQuery
     * @param param     param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.CourseDto>
     */
    TableDataInfo<CourseDto> getJoinCourseList(PageQuery pageQuery, CourseQueryParam param);

    /**
     * 根据课程码获取课程信息
     *
     * @param courseCode courseCode
     * @return com.olrtc.app.model.entity.Course
     * @author njy
     * @since 11:44 2023/3/15
     */
    Course getByCourseCode(String courseCode);


    /**
     * convert
     *
     * @param course
     * @return
     */
    CourseDto convertToDTO(Course course);

    /**
     * 查询加入课程用户数量
     *
     * @param courseId courseId
     * @return int
     */
    int getJoinCourseUserNumById(Integer courseId);


    /**
     * 用户已经开设的课程
     *
     * @param userId userId
     * @return java.util.List<com.olrtc.app.model.entity.Course>
     */
    List<Course> getUserCreateCurses(Integer userId);


    /**
     * 分页查询用户开设的课程信息
     *
     * @param pageQuery
     * @param param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.CourseDto>
     * @author njy
     * @since 09:53 2023/3/24
     */
    TableDataInfo<CourseDto> getCreatedCourseList(PageQuery pageQuery, CourseQueryParam param);

    /**
     * 分页查询公开课程信息
     *
     * @param pageQuery pageQuery
     * @param param     param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.CourseDto>
     */
    TableDataInfo<CourseDto> getOpenCourseList(PageQuery pageQuery, CourseQueryParam param);

    /**
     * 获取热门公开课程
     *
     * @return java.util.List<com.olrtc.app.model.dto.CourseDto>
     */
    List<CourseDto> getHotOpenCourse();

}
