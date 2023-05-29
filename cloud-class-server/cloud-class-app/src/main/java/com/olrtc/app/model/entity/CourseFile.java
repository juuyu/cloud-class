package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName t_course_file
 */
@TableName(value ="t_course_file")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseFile extends BaseEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 外键, 课程id
     */
    private Integer courseId;

    private String uploadUserName;

    /**
     * 文件名
     */
    private String fileName;

    private String cover;


    private String size;


    /**
     * 文件简介
     */
    private String fileIntro;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 文件下载地址
     */
    private String fileLink;

}