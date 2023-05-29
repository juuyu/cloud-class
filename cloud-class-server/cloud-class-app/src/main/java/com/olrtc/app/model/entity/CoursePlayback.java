package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @TableName t_course_playback
 */
@TableName(value = "t_course_playback")
@Data
@EqualsAndHashCode(callSuper = true)
public class CoursePlayback extends BaseEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 外键, 课程id
     */
    private Integer courseId;
    /**
     * 外键, 上传用户id
     */
    private Integer uploadUserId;
    /**
     * 回放名
     */
    private String  playbackName;
    /**
     * 封面
     */
    private String  cover;
    /**
     * 发布日期
     */
    private Date    uploadTime;
    /**
     * 回放文件地址
     */
    private String  playbackFileLink;

}