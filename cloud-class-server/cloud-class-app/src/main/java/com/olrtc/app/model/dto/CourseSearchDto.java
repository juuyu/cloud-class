package com.olrtc.app.model.dto;

import lombok.Data;


/**
 * @author njy
 * @since 2023/4/23 16:30
 */
@Data
public class CourseSearchDto {

    private Integer id;

    /**
     * 课程名称
     */
    private String courseName;

    private String cover;


}
