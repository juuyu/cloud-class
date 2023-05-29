package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olrtc.app.biz.RecordBiz;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.config.properties.MinIoProperties;
import com.olrtc.app.dao.VideoMapper;
import com.olrtc.app.enums.RecordStatus;
import com.olrtc.app.model.domain.RecordInfo;
import com.olrtc.app.model.dto.RecordDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.entity.Video;
import com.olrtc.app.model.param.RecordQueryParam;
import com.olrtc.app.model.param.RecordUpdateParam;
import com.olrtc.app.remote.RemoteFfmpegService;
import com.olrtc.app.service.UserService;
import com.olrtc.app.utils.MinioUtil;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.core.utils.file.FileUtil;
import com.olrtc.common.core.utils.file.MimeTypeUtil;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


/**
 * @author njy
 * @since 2023/3/13 16:18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordBizImpl implements RecordBiz {

    private final UserService         userService;
    private final RemoteFfmpegService remoteFfmpegService;
    private final MinIoProperties     minIoProperties;
    private final VideoMapper         videoMapper;

    private static final String playBackFilePath = "/Users/yu/my/docker/cloud-class/data/record/";

    private static final String fileSynthetic = "file '%s'\n";


    @Override
    public void uploadSliceFile(String recordId, MultipartFile slice) {

        // 异步任务修改录制信息
        CompletableFuture.runAsync(() -> {
            String recordInfoKey = RedisCacheKey.RECORD.RECORD_INFO + recordId;
            RecordInfo recordInfo = RedisUtil.getCacheObject(recordInfoKey);
            if (recordInfo == null) {
                log.error("record info is null");
                return;
            }
            if (!RecordStatus.START.equals(recordInfo.getRecordStatus())) {
                recordInfo.setRecordStartTime(new Date());
                recordInfo.setRecordStatus(RecordStatus.START);
                RedisUtil.setCacheObject(recordInfoKey, recordInfo);
//                RedisUtil.setCacheObject(recordInfoKey, recordInfo, Duration.ofDays(2));
            }
        });


        // slice: uuid-timeStamp.webm
        String originalFilename = slice.getOriginalFilename();

        //记录切片信息
        String fileSavePath = playBackFilePath + recordId + "/" + originalFilename;
        if (saveFile(slice, fileSavePath)) {
            String cacheKey = RedisCacheKey.RECORD.RECORD_CHUNK + recordId;
            RedisUtil.setCacheList(cacheKey, new ArrayList<String>() {{
                add(String.format(fileSynthetic, originalFilename));
            }});
        }
    }


    @Override
    public String startRecord(Integer userId) {
        User recordUser = userService.getById(userId);
        if (recordUser == null) {
            throw new BizException("无法开始录制,用户信息错误");
        }
        String recordID = UUID.randomUUID().toString();
        CompletableFuture.runAsync(() -> {
            String recordInfoKey = RedisCacheKey.RECORD.RECORD_INFO + recordID;
            // http://localhost:9000/cloud-class/record/bd3ce74e-e004-4fe8-9730-16c07684f2c8.webm
            String fileUrl = minIoProperties.getBaseUrl() + minIoProperties.getCommonBucket() +
                    "/record/" +
                    recordID +
                    ".webm";
            RecordInfo recordInfo = new RecordInfo()
                    .setRecordId(recordID)
                    .setRecordUser(Convert.convert(UserDto.class, recordUser))
                    .setRecordInitTime(new Date())
                    .setRecordStatus(RecordStatus.INIT)
                    .setRecordCoverUrl("http://localhost:9000/cloud-class/icon/playback.svg")
                    .setRecordUrl(fileUrl);
            RedisUtil.setCacheObject(recordInfoKey, recordInfo, Duration.ofDays(2));
        });
        return recordID;
    }

    @Override
    public void stopRecord(String recordId) {
//        try {
//            Thread.sleep(2 * 1000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // 异步任务修改录制信息
        CompletableFuture.runAsync(() -> {
            log.info("异步任务保存录制信息 => [recordId = {}]", recordId);
            String recordInfoKey = RedisCacheKey.RECORD.RECORD_INFO + recordId;
            RecordInfo recordInfo = RedisUtil.getCacheObject(recordInfoKey);
            if (recordInfo == null) {
                log.error("record info is null");
                return;
            }
            RedisUtil.deleteObject(recordInfoKey);
            recordInfo.setRecordEndTime(new Date());
            String recordCoverUrl = "http://localhost:9000/cloud-class/icon/playback.svg";
            if (recordInfo.getRecordCoverUrl() != null) {
                recordCoverUrl = recordInfo.getRecordCoverUrl();
            }
            Video video = new Video();
            video.setRecordId(recordInfo.getRecordId());
            video.setUserId(recordInfo.getRecordUser().getId());
            video.setVideoName(recordInfo.getRecordId());
            video.setRecordStart(recordInfo.getRecordStartTime());
            video.setRecordEnd(recordInfo.getRecordEndTime());
            video.setVideoCoverLink(recordCoverUrl);
            video.setVideoFileLink(recordInfo.getRecordUrl());
            video.setOpen(false);
            video.setWatchCount(0);

            log.info(JsonUtil.obj2String(video));
            int insert = videoMapper.insert(video);
            if (insert > 0) {
                log.info("录制信息保存成功 => [recordId = {}]", recordId);
            }
        });


        String cacheKey = RedisCacheKey.RECORD.RECORD_CHUNK + recordId;
        if (!RedisUtil.hasKey(cacheKey)) {
            return;
        }
        List<Object> fileList = RedisUtil.getCacheList(cacheKey);
        RedisUtil.deleteObject(cacheKey);
        if (fileList == null || CollectionUtils.isEmpty(fileList)) {
            log.warn("生成文件列表出错,redis中不存在数据,key:{}}", cacheKey);
            return;
        }
        StringBuilder content = new StringBuilder();
        fileList.forEach(content::append);
        String fileListName = playBackFilePath + "/" + recordId + "/" + recordId + ".txt";
        // save
        saveAsFileWriter(fileListName, content.toString());
        remoteFfmpegService.mergeVideo(recordId).subscribe(
                r -> {
                },
                err -> log.error("合成视频失败, err:", err)
        );
    }

    @Override
    public TableDataInfo<RecordDto> listUserRecord(PageQuery pageQuery, RecordQueryParam param) {
        Integer userId = param.getUserId();
        if (userId == null) {
            throw new BizException("用户不存在");
        }
        Boolean open = param.getOpen();
        String videoName = param.getVideoName();
        Integer recordTime = param.getRecordTime();
        LambdaQueryWrapper<Video> lqw = new LambdaQueryWrapper<Video>()
                .eq(Video::getUserId, userId)
                .eq(open != null, Video::getOpen, open)
                .like(StrUtil.isNotBlank(videoName), Video::getVideoName, videoName);
        if (recordTime != null) {
            Date newDate = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, recordTime);
            lqw.ge(Video::getRecordStart, newDate);
        }
        Page<Video> videoPage = videoMapper.selectPage(pageQuery.build(), lqw);
        IPage<RecordDto> recordDtoIPage = videoPage.convert(this::convert);
        return TableDataInfo.build(recordDtoIPage);
    }

    @Override
    public TableDataInfo<RecordDto> listOpenRecord(PageQuery pageQuery, RecordQueryParam param) {
        String videoName = param.getVideoName();
        Integer recordTime = param.getRecordTime();
        LambdaQueryWrapper<Video> lqw = new LambdaQueryWrapper<Video>()
                .eq(Video::getOpen, true)
                .like(StrUtil.isNotBlank(videoName), Video::getVideoName, videoName);
        if (recordTime != null) {
            Date newDate = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, recordTime);
            lqw.ge(Video::getRecordStart, newDate);
        }
        Page<Video> videoPage = videoMapper.selectPage(pageQuery.build(), lqw);
        IPage<RecordDto> recordDtoIPage = videoPage.convert(this::convert);
        return TableDataInfo.build(recordDtoIPage);
    }

    @Override
    public boolean recordIsGen(Integer videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            return false;
        }

        String videoFileLink = video.getVideoFileLink();
        if (StrUtil.isBlank(videoFileLink)) {
            return false;
        }
        // http://localhost:9000/cloud-class/record/bd3ce74e-e004-4fe8-9730-16c07684f2c8.webm
        String minioFileName = videoFileLink.replace(minIoProperties.getBaseUrl() + minIoProperties.getCommonBucket() + "/", "");
        return MinioUtil.fileExists(minIoProperties.getCommonBucket(), minioFileName);
    }

    @Override
    public void delRecord(Integer id) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            throw new BizException("该录制视频不存在");
        }
        videoMapper.deleteById(id);
    }

    @Override
    public boolean updateRecord(RecordUpdateParam recordUpdateParam) {
        Video video = videoMapper.selectById(recordUpdateParam.getVideoId());
        if (video == null) {
            throw new BizException("该录制视频不存在");
        }
        String videoName = recordUpdateParam.getVideoName();
        String videoIntro = recordUpdateParam.getVideoIntro();
        Boolean open = recordUpdateParam.getOpen();
        String videoCoverLink = recordUpdateParam.getVideoCoverLink();
        if (StrUtil.isNotBlank(videoName))
            video.setVideoName(videoName);
        if (StrUtil.isNotBlank(videoIntro))
            video.setVideoIntro(videoIntro);
        if (StrUtil.isNotBlank(videoCoverLink))
            video.setVideoCoverLink(videoCoverLink);
        if (open != null)
            video.setOpen(open);

        int i = videoMapper.updateById(video);
        return i > 0;
    }

    @Override
    public void uploadRecordCover(String recordId, MultipartFile cover) {
        if (StrUtil.isBlank(recordId)) {
            throw new BizException("recordId不能为空");
        }
        if (cover == null) {
            throw new BizException("cover不能为空");
        }
        try {
            String originalFilename = cover.getOriginalFilename();
            String newFileName = "file/images/" +
                    UUID.randomUUID().toString().replace("-", "") +
                    "." +
                    FileUtil.getExtension(originalFilename);
            // 保存到redis
            CompletableFuture.runAsync(() -> {
                String recordInfoKey = RedisCacheKey.RECORD.RECORD_INFO + recordId;
                RecordInfo recordInfo = RedisUtil.getCacheObject(recordInfoKey);
                if (recordInfo == null) {
                    log.error("record info is null");
                    return;
                }
                String recordCoverUrl = minIoProperties.getBaseUrl() +
                        minIoProperties.getCommonBucket() + "/" + newFileName;
                recordInfo.setRecordCoverUrl(recordCoverUrl);
                RedisUtil.setCacheObject(recordInfoKey, recordInfo);
            });
            // 保存到minio
            InputStream inputStream = cover.getInputStream();
            MinioUtil.upload(newFileName, inputStream, MimeTypeUtil.probeContentType(originalFilename));
        } catch (IOException e) {
            log.error("上传封面失败", e);
        }
    }


    private RecordDto convert(Video video) {
        RecordDto recordDto = Convert.convert(RecordDto.class, video);
        recordDto.setRecordStart(DateUtil.formatDateTime(video.getRecordStart()));
        recordDto.setRecordEnd(DateUtil.formatDateTime(video.getRecordEnd()));
        Integer userId = video.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            recordDto.setUserName(user.getUserName());
            recordDto.setUserAvatar(user.getAvatar());
            recordDto.setUserRealName(user.getRealName());
        }
        return recordDto;
    }

    public static boolean saveFile(MultipartFile slice, String path) {
        File file = new File(path);
        // file exists ?
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                slice.transferTo(file);
                return true;
            } catch (Exception e) {
                log.error("文件上传失败,path:{}", path, e);
            }
        } catch (Exception e) {
            log.warn("文件夹创建失败,path={}", path, e);
        }
        return false;
    }

    public static void saveAsFileWriter(String path, String content) {
        File file = new File(path);
        // file exists ?
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (Exception e) {
            log.warn("文件夹创建失败,path={}", path);
        }
        // save
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(path));
            out.write(content);
            out.close();
        } catch (IOException e) {
            log.warn("文件创建失败,path={}", path);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.error("流关闭错误");
                }
            }
        }
    }

}
