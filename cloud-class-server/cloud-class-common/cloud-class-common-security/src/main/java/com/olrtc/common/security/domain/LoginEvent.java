package com.olrtc.common.security.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author njy
 * @since 2023/2/17 13:24
 */
@Data
public class LoginEvent implements Serializable {
    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * ip地址
     */
    private String ipaddr;

    /**
     * 提示消息
     */
    private String msg;

}
