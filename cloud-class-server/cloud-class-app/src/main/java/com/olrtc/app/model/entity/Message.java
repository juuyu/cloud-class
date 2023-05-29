package com.olrtc.app.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olrtc.app.enums.MessageType;
import com.olrtc.common.mybatis.mode.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @TableName t_message
 */
@TableName(value = "t_message")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Message extends BaseEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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
    private Date sendTime;

    /**
     * 外键, 用户id
     */
    private Integer sendId;

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