package com.olrtc.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.config.properties.MinIoProperties;
import com.olrtc.app.dao.VideoMapper;
import com.olrtc.app.model.entity.Video;
import com.olrtc.app.service.VideoService;
import com.olrtc.app.utils.MinioUtil;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yu
 * @description 针对表【t_video】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
        implements VideoService {

    private final MinIoProperties minIoProperties;

    @Override
    public String genM3U8File(String recordId) {
        if (StrUtil.isBlank(recordId)) {
            log.error("生成回放列表出错,recordId为空");
            return null;
        }

        String m3u8FileKey = RedisCacheKey.LIVE.RECORD + recordId;
        List<String> m3u8List = RedisUtil.getCacheList(m3u8FileKey);
        if (m3u8List == null || m3u8List.size() == 0) {
            log.error("生成回放列表出错,redis中不存在数据,recordId:{}", recordId);
            return null;
        }

        //生成文件
        StringBuilder content = new StringBuilder();
        m3u8List.forEach(content::append);
        content.append("#EXT-X-ENDLIST");
        String fileName = "/record/" + recordId + "/" + recordId + ".m3u8";
        //upload
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8));
            MinioUtil.upload(fileName, in, "application/x-mpegURL");
            log.info("生成文件成功,fileName:{}", fileName);
            RedisUtil.deleteObject(m3u8FileKey);
            return minIoProperties.getBaseUrl()
                    + minIoProperties.getCommonBucket()
                    + fileName;
        } catch (Exception e) {
            log.error("上传播放列表失败,fileName:[{}]", fileName, e);
        }
        return null;
    }
}




