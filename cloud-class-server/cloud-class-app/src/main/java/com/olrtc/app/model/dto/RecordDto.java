package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author njy
 * @since 2023/4/21 17:44
 */
@Data
@Accessors(chain = true)
public class RecordDto {
    /**
     *
     */
    private Integer id;

    /**
     * recordId
     */
    private String recordId;

    /**
     * 外键, 用户id
     */
    private Integer userId;

    private String userName;
    private String userAvatar;
    private String userRealName;

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
    private String recordStart;

    /**
     * 结束录制时间
     */
    private String recordEnd;

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


}
