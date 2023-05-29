package com.olrtc.app.web;

import com.olrtc.app.biz.RecordBiz;
import com.olrtc.app.model.dto.RecordDto;
import com.olrtc.app.model.param.RecordQueryParam;
import com.olrtc.app.model.param.RecordUpdateParam;
import com.olrtc.app.service.HotService;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author njy
 * @since 2023/3/3 16:48
 */
@Slf4j
@RestController
@RequestMapping("record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordBiz recordBiz;
    private final HotService hotService;

    /**
     * 录制切片上传接口
     *
     * @param slice slice
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:20 2023/3/13
     */
    @PostMapping("upload")
    public R<Void> sliceUpload(@RequestParam("recordId") String recordId,
                               @RequestParam("slice") MultipartFile slice) {
        if (StrUtil.isBlank(recordId)) {
            throw new BizException("recordId不能为空");
        }
        recordBiz.uploadSliceFile(recordId, slice);
        return R.ok();
    }

    /**
     * 开始录制
     *
     * @param userId userId
     * @return com.olrtc.common.core.domain.R<java.lang.String>
     * @author njy
     * @since 17:34 2023/4/18
     */
    @GetMapping("start")
    public R<String> startRecord(Integer userId) {
        log.info("startRecord() called with parameters => [userId = {}]", userId);
        String res = recordBiz.startRecord(userId);
        return R.ok("ok", res);
    }


    /**
     * 结束录制
     *
     * @param recordId recordId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 17:34 2023/4/18
     */
    @GetMapping("stop")
    public R<Void> stopRecord(String recordId) {
        log.info("stopRecord() called with parameters => [recordId = {}]", recordId);
        recordBiz.stopRecord(recordId);
        return R.ok("ok");
    }


    /**
     * 更新录制封面
     *
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @PostMapping("cover")
    public R<Void> uploadRecordCover(@RequestParam("recordId") String recordId,
                                     @RequestParam("cover") MultipartFile cover) {
        log.info("uploadRecordCover() called with parameters => [recordId = {}]",recordId);
        recordBiz.uploadRecordCover(recordId, cover);
        return R.ok("ok");
    }

    /**
     * 分页查询用户录制视频信息
     *
     * @param pageQuery pageQuery
     * @param param     param
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.RecordDto>>
     * @author njy
     * @since 10:52 2023/4/23
     */
    @GetMapping("video")
    public R<TableDataInfo<RecordDto>> listUserRecord(PageQuery pageQuery, RecordQueryParam param) {
        log.info("listUserRecord() called with parameters => [pageQuery = {}], [param = {}]", pageQuery, param);
        TableDataInfo<RecordDto> videos = recordBiz.listUserRecord(pageQuery, param);
        return R.ok("查询成功", videos);
    }


    /**
     * 修改录制视频信息
     *
     * @param recordUpdateParam recordUpdateParam
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @PostMapping("update")
    public R<Void> updateRecord(@RequestBody @Validated RecordUpdateParam recordUpdateParam) {
        log.info("updateRecord() called with parameters => [recordUpdateParam = {}]", recordUpdateParam);
        boolean f = recordBiz.updateRecord(recordUpdateParam);
        return f ? R.ok("修改成功") : R.fail("修改失败");
    }


    /**
     * 删除录制视频
     *
     * @param id id
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @GetMapping("del")
    public R<Void> delRecord(Integer id) {
        log.info("delRecord() called with parameters => [id = {}]", id);
        recordBiz.delRecord(id);
        return R.ok("删除成功");
    }


    /**
     * 查询视频是否生成
     *
     * @param videoId videoId
     * @return com.olrtc.common.core.domain.R<java.lang.Boolean>
     * @author njy
     * @since 10:53 2023/4/23
     */
    @GetMapping("video/gen")
    public R<Boolean> recordIsGen(Integer videoId) {
        log.info("recordIsGen() called with parameters => [videoId = {}]", videoId);
        boolean f = recordBiz.recordIsGen(videoId);
        if (f){
            Thread.startVirtualThread(() -> {
                // 更新热度
                hotService.videoWatchCountPlusOne(videoId);
            });
        }
        return R.ok("ok", f);
    }

}
