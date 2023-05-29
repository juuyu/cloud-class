package com.olrtc.app.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/14 22:43
 */
@Data
@Accessors(chain = true)
public class CourseFileDto {

    /**
     *
     */
    private Integer id;

    /**
     * 外键, 课程id
     */
    private Integer courseId;

    /**
     * 文件名
     */
    private String fileName;


    private String cover;


    private String size;

    private String uploadUserName;

    /**
     * 文件简介
     */
    private String fileIntro;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 文件下载地址
     */
    private String fileLink;

}
