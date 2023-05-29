package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import com.olrtc.app.biz.UserBiz;
import com.olrtc.app.model.domain.RegisterBody;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.param.UserUpdateParam;
import com.olrtc.app.service.UserService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.exception.ServiceException;
import com.olrtc.common.core.utils.Md5Util;
import com.olrtc.common.security.domain.LoginUser;
import com.olrtc.common.security.utils.LoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author njy
 * @since 2023/4/17 13:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBizImpl implements UserBiz {
    private final        UserService userService;
    private static final String      salt = "WClhAKSaDNZybJR2";

    @Override
    public UserDto login(String userName, String password) {
        User user = userService.getUserInfoByUserName(userName);
        if (user == null) {
            throw new ServiceException("登录失败,该用户不存在");
        }
        String pwdEncode = Md5Util.MD5EncodeUseSalt(password, salt);
        if (!user.getPassword().equals(pwdEncode)) {
            throw new ServiceException("登录失败,密码错误");
        }
        LoginUser loginUser = buildLoginUser(user);
        LoginUtil.login(loginUser);
        return Convert.convert(UserDto.class, user);
    }

    @Override
    public boolean register(RegisterBody registerBody) {
        String userName = registerBody.getUserName();
        if (userService.getUserInfoByUserName(userName) != null) {
            throw new BizException("该账号已存在,无法注册");
        }
        String pwdEncode = Md5Util.MD5EncodeUseSalt(registerBody.getPassword(), salt);

        User user = new User();
        user.setUserName(userName);
        user.setPassword(pwdEncode);
        user.setEmail(registerBody.getEmail());
        return userService.save(user);
    }

    @Override
    public void logout() {
        LoginUtil.logout();
    }

    @Override
    public UserDto updateUserInfo(UserUpdateParam userUpdateParam) {
        Integer userId = userUpdateParam.getUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BizException("该用户不存在");
        }
        user.setRealName(userUpdateParam.getRealName());
        user.setAge(userUpdateParam.getAge());
        user.setSex(userUpdateParam.getSex());
        user.setTel(userUpdateParam.getTel());
        boolean f = userService.updateById(user);
        if (!f){
            throw new BizException("更新用户信息失败");
        }
        return Convert.convert(UserDto.class, user);
    }

    private LoginUser buildLoginUser(User user) {
        return new LoginUser()
                .setId(user.getId())
                .setUserName(user.getUserName())
                .setPassword(user.getPassword())
                .setEmail(user.getEmail())
                .setAge(user.getAge())
                .setAvatar(user.getAvatar())
                .setSex(user.getSex())
                .setUserType(user.getUserType())
                .setTel(user.getTel())
                .setRealName(user.getRealName());
    }
}
