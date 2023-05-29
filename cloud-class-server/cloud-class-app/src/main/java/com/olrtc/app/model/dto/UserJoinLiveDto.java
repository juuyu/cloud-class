package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/3/15 16:44
 */
@Data
@Accessors(chain = true)
public class UserJoinLiveDto {

    /**
     * 房间号
     */
    private String roomId;


    /**
     * 摄像头live地址
     */
    private String userVideoPlay;


    /**
     * 屏幕live地址
     */
    private String screenVideoPlay;

    /**
     * 课程id
     */
    private Integer courseId;

}
