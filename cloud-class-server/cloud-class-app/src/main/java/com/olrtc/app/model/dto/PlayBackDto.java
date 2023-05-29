package com.olrtc.app.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/14 22:58
 */
@Data
@Accessors(chain = true)
public class PlayBackDto {
    /**
     *
     */
    private Integer id;

    /**
     * 外键, 课程id
     */
    private Integer courseId;

    /**
     * 回放名
     */
    private String playbackName;


    private String cover;

    private String uploadUserName;

    /**
     * 发布日期
     */
    private String uploadTime;

    /**
     * 回放文件地址
     */
    private String playbackFileLink;

}
