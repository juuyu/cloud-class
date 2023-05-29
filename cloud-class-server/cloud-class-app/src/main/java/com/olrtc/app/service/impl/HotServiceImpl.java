package com.olrtc.app.service.impl;

import com.olrtc.app.dao.VideoMapper;
import com.olrtc.app.model.entity.Video;
import com.olrtc.app.service.HotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author njy
 * @since 2023/5/2 00:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotServiceImpl implements HotService {
    private final VideoMapper videoMapper;

    @Override
    public void videoWatchCountPlusOne(Integer videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video != null) {
            Integer watchCount = video.getWatchCount();
            video.setWatchCount(watchCount != null ? watchCount + 1 : 0);
            videoMapper.updateById(video);
        }
    }
}
