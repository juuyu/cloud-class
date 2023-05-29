package com.olrtc.app.biz;

import com.olrtc.app.model.domain.RegisterBody;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.param.UserUpdateParam;

/**
 * @author njy
 * @since 2023/2/16 17:21
 */
public interface UserBiz {

    /**
     * 用户登录
     *
     * @param userName userName
     * @param password password
     * @return com.olrtc.app.model.dto.UserDto
     * @author njy
     * @since 13:43 2023/4/17
     */
    UserDto login(String userName, String password);

    /**
     * 用户注册
     *
     * @param registerBody registerBody
     * @return com.olrtc.app.model.dto.UserDto
     * @author njy
     * @since 13:43 2023/4/17
     */
    boolean register(RegisterBody registerBody);


    /**
     * 注销
     *
     * @author njy
     * @since 13:43 2023/4/17
     */
    void logout();


    /**
     * 更新用户信息
     *
     * @param userUpdateParam
     * @return com.olrtc.app.model.dto.UserDto
     * @author njy
     * @since 16:23 2023/4/27
     */
    UserDto updateUserInfo(UserUpdateParam userUpdateParam);
}
