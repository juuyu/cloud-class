package com.olrtc.app.model.dto;

import com.olrtc.app.enums.MessageType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/4/10 16:24
 */
@Data
@Accessors(chain = true)
public class UserMessageDto {
    /**
     *
     */
    private Integer id;

    /**
     * 信息名称
     */
    private String msgName;

    /**
     * 信息类型
     */
    private MessageType msgType;

    /**
     * 信息状态, 是否已读
     */
    private Boolean msgRead;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 外键, 用户id
     */
    private Integer sendId;

    private String sendRealName;
    private String sendUserName;

    /**
     * 外键, 用户id
     */
    private Integer receiveId;

    /**
     * 信息详情
     */
    private String msgContent;

    /**
     * 是否需要回复, 默认不需要
     */
    private Boolean needReply;

    /**
     * 确认地址
     */
    private String confirmUrl;

    /**
     * 取消地址
     */
    private String refuseUrl;
}
