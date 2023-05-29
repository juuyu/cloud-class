package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/4/6 13:49
 */
@Data
@Accessors(chain = true)
public class CourseJobCommitDto {
    /**
     *
     */
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
    private String commitTime;

    /**
     * 提交附件下载地址
     */
    private String  commitFileLink;
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
