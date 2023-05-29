package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @TableName t_course_job_commit
 */
@TableName(value = "t_course_job_commit")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseJobCommit extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键, 作业id
     */
    private Integer courseJobId;

    /**
     * 外键, 学生id
     */
    private Integer studentId;

    /**
     * 提交内容
     */
    private String content;

    /**
     * 提交日期
     */
    private Date commitTime;

    /**
     * 提交附件下载地址
     */
    private String  commitFileLink;
    private String  summaryPath;
    private String  summaryLink;
    private Boolean aiCheck;

    /**
     * 状态枚举, 1:未批改, 2:已批改, 3:驳回
     */
    private Integer status;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 批改后内容
     */
    private String comment;

}