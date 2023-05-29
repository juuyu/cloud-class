package com.olrtc.app.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olrtc.app.enums.CourseType;
import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/2/28 11:49
 */
@Data
public class CourseAddParam {

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程简介
     */
    private String intro;

    /**
     * 课程类型
     */
    private CourseType courseType;

    private String cover;

    /**
     * 课程开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date startTime;

    /**
     * 课程结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date endTime;

    /**
     * 加入课程的截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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

}
