package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author njy
 * @since 2023/5/4 14:26
 */
@Data
@Accessors(chain = true)
public class UserTodayCourseDto {
    /**
     * 学生课程数量
     */
    private int stuCourseNum;
    /**
     * 教师课程数量
     */
    private int teaCourseNum;
    /**
     * 学生课程列表
     */
    private List<CourseDayArrangeDto> stuCourseList;
    /**
     * 教师课程列表
     */
    private List<CourseDayArrangeDto> teaCourseList;

}
