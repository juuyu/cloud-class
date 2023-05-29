package com.olrtc.app.model.dto;

import com.olrtc.app.enums.CourseStatus;
import com.olrtc.app.enums.CourseType;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author njy
 * @since 2023/2/27 23:14
 */
@Data
@Accessors(chain = true)
public class CourseDto {

    /**
     *
     */
    private Integer id;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;


    /**
     * 课程简介
     */
    private String intro;

    /**
     * 课程类型
     */
    private CourseType courseType;

    /**
     * 课程封面
     */
    private String cover;

    /**
     * 外键, 教师id
     */
    private Integer teacherId;

    private String teacherName;


    /**
     * 课程开始日期
     */
    private String startTime;

    /**
     * 课程结束日期
     */
    private String endTime;



    private CourseStatus courseStatus;

    /**
     * 加入课程的截止时间
     */
    private String joinEndTime;

    /**
     * 课程每周安排
     */
    private String weekArrange;

    /**
     * 课程上限人数
     */
    private Integer userNum;


    private int courseUserNum;

    private Boolean needReview;
    private Boolean needOpen;


}
