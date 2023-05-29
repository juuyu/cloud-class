package com.olrtc.app.model.param;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/23 16:56
 */
@Data
public class RecordUpdateParam {
    /**
     * 视频id
     */
    private Integer videoId;
    /**
     * 视频名称
     */
    private String  videoName;
    /**
     * 视频简介
     */
    private String  videoIntro;
    /**
     * 视频封面
     */
    private String  videoCoverLink;
    /**
     * 是否公开
     */
    private Boolean open;
}
