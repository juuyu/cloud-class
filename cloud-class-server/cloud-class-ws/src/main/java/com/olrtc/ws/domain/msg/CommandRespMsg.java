package com.olrtc.ws.domain.msg;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/3/23 11:58
 */
@Data
@Accessors(chain = true)
public class CommandRespMsg {

    // 发送时间
    private String sendTimeTimestamp;

    // 摄像头开关
    private boolean camera;

    // 屏幕开关
    private boolean screen;

    // 是否结束
    private boolean exit;
}
