package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @TableName t_video
 */
@TableName(value = "t_video")
@Data
@EqualsAndHashCode(callSuper = true)
public class Video extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * recordId
     */
    private String recordId;

    /**
     * 外键, 用户id
     */
    private Integer userId;

    /**
     * 视频名
     */
    private String videoName;

    /**
     * 视频简介
     */
    private String videoIntro;

    /**
     * 开始录制时间
     */
    private Date recordStart;

    /**
     * 结束录制时间
     */
    private Date recordEnd;

    /**
     * 是否公开
     */
    private Boolean open;

    /**
     * 视频文件地址
     */
    private String videoFileLink;

    /**
     * 视频封面地址
     */
    private String videoCoverLink;
    /**
     * 观看次数
     */
    private Integer watchCount;
}