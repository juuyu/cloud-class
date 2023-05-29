package com.olrtc.app.model.param;

import com.olrtc.app.enums.CourseStatus;
import com.olrtc.app.enums.CourseType;
import lombok.Data;

/**
 * @author njy
 * @since 2023/2/27 23:14
 */
@Data
public class CourseQueryParam {

    /**
     * 课程名
     */
    private String courseName;
    /**
     * 课程类型
     */
    private CourseType courseType;
    /**
     * 课程状态
     */
    private CourseStatus courseStatus;

}
