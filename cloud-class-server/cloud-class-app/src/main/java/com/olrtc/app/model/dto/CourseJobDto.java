package com.olrtc.app.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/14 22:57
 */
@Data
@Accessors(chain = true)
public class CourseJobDto {
    /**
     *
     */
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
    private String startTime;

    /**
     * 作业结束日期
     */
    private String endTime;

    /**
     * 作业附件下载地址
     */
    private String jobFileLink;
}
