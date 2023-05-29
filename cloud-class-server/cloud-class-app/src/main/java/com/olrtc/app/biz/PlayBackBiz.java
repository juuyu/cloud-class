package com.olrtc.app.biz;

import com.olrtc.app.model.dto.PlayBackDto;
import com.olrtc.app.model.param.PlayBackAddParam;

import java.util.List;

/**
 * @author njy
 * @since 2023/3/14 22:58
 */
public interface PlayBackBiz {

    /**
     *
     * @author njy
     * @since 10:23 2023/4/3
     * @param courseId
     * @return java.util.List<com.olrtc.app.model.dto.PlayBackDto>
     */
    List<PlayBackDto> getPlayBacks(Integer courseId);

    /**
     *
     * @author njy
     * @since 10:23 2023/4/3
     * @param id
     * @return void
     */
    void delPlayBack(Integer id);

    /**
     * 添加课程回放
     *
     * @param param param
     * @return boolean
     */
    boolean addPlayBack(PlayBackAddParam param);
}
