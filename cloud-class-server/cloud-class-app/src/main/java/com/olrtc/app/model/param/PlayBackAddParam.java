package com.olrtc.app.model.param;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/24 09:53
 */
@Data
public class PlayBackAddParam {
    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 课程id
     */
    private Integer[] courseIds;
}
