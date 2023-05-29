package com.olrtc.app.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/3/28 17:33
 */
@Data
@Accessors(chain = true)
public class CourseUpdateParam {

    private Integer id;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程简介
     */
    private String intro;


    private String cover;

    /**
     * 课程开始日期
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date startTime;
//
//    /**
//     * 课程结束日期
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date endTime;
//
//    /**
//     * 加入课程的截止时间
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date joinEndTime;

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
