package com.olrtc.common.security.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.security.domain.LoginUser;

/**
 * @author njy
 * @since 2023/2/17 09:14
 */
public class LoginUtil {

    public static final String JOIN_CODE      = ":";
    public static final String LOGIN_USER_KEY = "loginUser";

    public static void login(LoginUser loginUser) {
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.login(loginUser.getId());
        setLoginUser(loginUser);
    }

    /**
     * 设置用户数据(多级缓存)
     */
    public static void setLoginUser(LoginUser loginUser) {
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    public static LoginUser getLoginUser() {
        try {
            LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
            if (loginUser != null) {
                return loginUser;
            }
            loginUser = (LoginUser) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
            SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
            return loginUser;
        } catch (Exception e) {
            throw new BizException("token异常, 校验失败");
        }
    }

    /**
     * 获取用户账号
     */
    public static String getUserName() {
        LoginUser loginUser = getLoginUser();
        return loginUser.getUserName();
    }


    public static Integer getUserId() {
        LoginUser loginUser = getLoginUser();
        return loginUser.getId();
    }

    public static Integer getUserIdOrDefault() {
        Integer userId;
        try {
            userId = getUserId();
        } catch (Exception e) {
            userId = -1;
        }
        return userId;
    }


    //获取用户token
    public static String getUserToken() {
        return StpUtil.getTokenValue();
    }

    public static void logout() {
        StpUtil.logout();
    }


}
