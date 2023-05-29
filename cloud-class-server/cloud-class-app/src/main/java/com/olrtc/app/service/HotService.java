package com.olrtc.app.service;

/**
 * @author njy
 * @since 2023/5/2 00:42
 */
public interface HotService {
    /**
     * 视频热度加一
     *
     * @param videoId 视频id
     */
    void videoWatchCountPlusOne(Integer videoId);
}
