package com.olrtc.app.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/4/3 17:03
 */
@Data
public class CourseJobAddParam {
    /**
     * 外键, 课程id
     */
    private Integer courseId;

    /**
     * 作业名
     */
    private String jobName;


    /**
     * 作业详情
     */
    private String content;

    /**
     * ai 批阅
     */
    private Boolean aiReview;

    /**
     * 作业结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date endTime;

    /**
     * 作业附件下载地址
     */
    private String jobFileLink;
}
