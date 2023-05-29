package com.olrtc.app.model.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/4/1 16:47
 */
@Data
public class CourseFileAddParam {

    private Integer courseId;

    /**
     * 文件名
     */
    private String fileName;


    private String trueFileName;


    private String size;
    /**
     * 文件简介
     */
    private String fileIntro;
    /**
     * 文件下载地址
     */
    private String fileLink;

}
