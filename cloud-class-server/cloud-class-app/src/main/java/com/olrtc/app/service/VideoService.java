package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.entity.Video;

/**
 * @author yu
 * @description 针对表【t_video】的数据库操作Service
 * @createDate 2023-02-22 10:56:10
 */
public interface VideoService extends IService<Video> {

    /**
     * 生成m3u8文件
     *
     * @param recordId
     * @return boolean
     * @author njy
     * @since 23:03 2023/5/5
     */
    String genM3U8File(String recordId);
}
