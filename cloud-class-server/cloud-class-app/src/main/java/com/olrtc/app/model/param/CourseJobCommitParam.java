package com.olrtc.app.model.param;

import lombok.Data;


/**
 * @author njy
 * @since 2023/4/3 17:05
 */
@Data
public class CourseJobCommitParam {

    private Integer courseJobCommitId;
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
     * 提交附件下载地址
     */
    private String commitFileLink;

}
