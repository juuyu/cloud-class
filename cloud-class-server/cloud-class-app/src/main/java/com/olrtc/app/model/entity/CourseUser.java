package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.olrtc.app.enums.JoinType;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName t_course_user
 */
@TableName(value ="t_course_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseUser extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键, 课程id
     */
    private Integer courseId;

    /**
     * 外键, 学生id
     */
    private Integer studentId;


    private JoinType joinType;

    /**
     * 选课时间
     */
    private Date selectedTime;


    /**
     * 状态枚举, 1:未审核, 2:通过, 3:驳回
     */
    private Integer status;

}