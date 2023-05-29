package com.olrtc.app.web;

import com.olrtc.app.biz.PlayBackBiz;
import com.olrtc.app.model.dto.PlayBackDto;
import com.olrtc.app.model.param.PlayBackAddParam;
import com.olrtc.app.random.ShortUID;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:57
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("course/playback")
public class PlayBackController {
    private final PlayBackBiz playBackBiz;

    /**
     * 获取课程回放
     *
     * @param courseId
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.PlayBackDto>>
     * @author njy
     * @since 23:07 2023/3/14
     */
    @GetMapping
    public R<List<PlayBackDto>> getPlayBacks(Integer courseId) {
        List<PlayBackDto> playBacks = playBackBiz.getPlayBacks(courseId);
        return R.ok("查询成功", playBacks);
    }


    /**
     * 添加课程回放
     *
     * @param param param
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     */
    @PostMapping
    public R<Void> addPlayBack(@RequestBody PlayBackAddParam param) {
        log.info("addPlayBack() called with parameters => [param = {}]", param);
        boolean f = playBackBiz.addPlayBack(param);
        return f ? R.ok("添加成功") : R.fail("添加失败");
    }

    /**
     * 删除课程文件
     *
     * @param id
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 10:21 2023/4/3
     */
    @GetMapping("del")
    public R<Void> delCourseFile(Integer id) {
        log.info("delCourseFile() called with parameters => [id = {}]", id);
        playBackBiz.delPlayBack(id);
        return R.ok("删除成功");
    }


    /**
     * share
     *
     * @param videoUrl videoUrl
     * @param expireDay expireDay
     * @return com.olrtc.common.core.domain.R<java.lang.String>
     * @author njy
     * @since 13:48 2023/4/24
     */
    @GetMapping("share")
    public R<String> getVideoShareLink(String videoUrl, Integer expireDay) {
        String shortUid = ShortUID.genThreeDigitSeed(videoUrl, expireDay);
        return R.ok("", shortUid);
    }


}
