package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName t_course_job
 */
@TableName(value = "t_course_job")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseJob extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键, 课程id
     */
    private Integer courseId;

    /**
     * 作业名
     */
    private String jobName;

    /**
     * 作业简介
     */
    private String jobIntro;

    /**
     * 作业详情
     */
    private String content;

    /**
     * ai 批阅
     */
    private Boolean aiReview;

    /**
     * 作业发布日期
     */
    private Date startTime;

    /**
     * 作业结束日期
     */
    private Date endTime;

    /**
     * 作业附件下载地址
     */
    private String jobFileLink;

}