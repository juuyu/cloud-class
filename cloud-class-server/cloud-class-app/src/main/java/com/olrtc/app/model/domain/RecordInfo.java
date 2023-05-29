package com.olrtc.app.model.domain;

import com.olrtc.app.enums.RecordStatus;
import com.olrtc.app.model.dto.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author njy
 * @since 2023/4/19 13:14
 */
@Data
@Accessors(chain = true)
public class RecordInfo {
    // 录制id
    private String       recordId;
    // 录制人信息
    private UserDto      recordUser;
    // 录制初始化时间
    private Date         recordInitTime;
    // 录制开始时间
    private Date         recordStartTime;
    // 录制结束时间
    private Date         recordEndTime;
    // 录制状态
    private RecordStatus recordStatus;
    // 录制视频地址
    private String       recordUrl;
    // 录制视频封面
    private String       recordCoverUrl;
}
