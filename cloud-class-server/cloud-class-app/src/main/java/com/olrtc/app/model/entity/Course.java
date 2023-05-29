package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.app.enums.CourseType;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 
 * @TableName t_course
 */
@TableName(value ="t_course")
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
     * 课程类型, ONE_ONE(1,"一对一"),ONE_MANY(2,"一对多");
     */
    private CourseType courseType;

    private String cover;

    /**
     * 外键, 教师id
     */
    private Integer teacherId;

    /**
     * 开课年度
     */
    private String session;

    /**
     * 课程开始日期
     */
    private Date startTime;

    /**
     * 课程结束日期
     */
    private Date endTime;

    /**
     * 加入课程的截止时间
     */
    private Date joinEndTime;

    /**
     * 课程每周安排
     */
    private String weekArrange;

    /**
     * 课程上限人数
     */
    private Integer userNum;

    /**
     * 是否需要审核
     */
    private Boolean needReview;

    /**
     * 是否公开
     */
    private Boolean needOpen;

    /**
     * 状态枚举, 1:未审核, 2:通过, 3:驳回
     */
    private Integer status;

}