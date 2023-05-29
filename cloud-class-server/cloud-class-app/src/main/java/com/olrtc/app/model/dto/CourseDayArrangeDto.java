package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/5/4 15:26
 */
@Data
@Accessors(chain = true)
public class CourseDayArrangeDto {
    /**
     * 课程id
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
     * 课程今日安排
     */
    private String dayArrange;
}
