package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/3/15 17:57
 */
@Data
@Accessors(chain = true)
public class TeaStartLiveDto {
    /**
     * 房间号
     */
    private String roomId;

    /**
     * 白板url
     */
    public String whiteboardUrl;

    /**
     * 摄像头publish地址
     */
    private String userVideoPublish;


    /**
     * 屏幕publish地址
     */
    private String screenVideoPublish;


    /**
     * 外键, 课程id
     */
    private Integer courseId;
}
