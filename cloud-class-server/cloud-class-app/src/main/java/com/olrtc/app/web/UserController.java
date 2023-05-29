package com.olrtc.app.web;

import com.olrtc.app.biz.MailBiz;
import com.olrtc.app.biz.UserBiz;
import com.olrtc.app.model.domain.LoginBody;
import com.olrtc.app.model.domain.RegisterBody;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.param.UserUpdateParam;
import com.olrtc.app.service.UserService;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author njy
 * @since 2023/2/16 15:52
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserBiz     userBiz;
    private final UserService userService;
    private final MailBiz     mailBiz;


    /**
     * 登录
     *
     * @param form
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 16:13 2023/4/27
     */
    @PostMapping("login")
    public R<UserDto> login(@Validated @RequestBody LoginBody form) {
        log.info("login() called with parameters => [form = {}]", form);
        UserDto userDTO = userBiz.login(form.getUsername(), form.getPassword());
        if (userDTO == null) {
            return R.fail("登录失败");
        }
        return R.ok("登录成功", userDTO);
    }

    /**
     * 登出
     *
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 16:14 2023/4/27
     */
    @PostMapping("logout")
    public R<Void> logout() {
        userBiz.logout();
        return R.ok();
    }


    /**
     * 用户注册
     */
    @PostMapping("register")
    public R<Void> register(@Validated @RequestBody RegisterBody registerBody) {
        boolean isValidEmail = mailBiz.checkEmailCode(registerBody.getEmail(), registerBody.getEmailCode());
        if (!isValidEmail) {
            return R.fail("邮箱验证码错误");
        }
        boolean f = userBiz.register(registerBody);
        return f ? R.ok("注册成功") : R.fail("注册失败");
    }


    /**
     * 查询用户
     *
     * @param queryParam
     * @return com.olrtc.common.core.domain.R<java.util.List < com.olrtc.app.model.dto.UserDto>>
     * @author njy
     * @since 16:42 2023/3/29
     */
    @GetMapping("user/query")
    public R<List<UserDto>> queryUser(String queryParam) {
        List<UserDto> users = userService.queryUser(queryParam);
        return R.ok("ok", users);
    }

    /**
     * 修改用户头像
     *
     * @param userId
     * @param avatarUrl
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 16:15 2023/4/27
     */
    @PostMapping("user/avatar")
    public R<UserDto> changeUserAvatar(Integer userId, String avatarUrl) {
        if (userId == null) {
            throw new BizException("用户id不能为null");
        }
        if (StrUtil.isBlank(avatarUrl)) {
            throw new BizException("头像地址不能为null");
        }
        UserDto userDto = userService.changeUserAvatar(userId, avatarUrl);
        return userDto != null ? R.ok("修改成功", userDto) : R.fail("修改失败");
    }


    /**
     * 修改用户信息
     *
     * @param userUpdateParam
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 16:23 2023/4/27
     */
    @PostMapping("user/update")
    public R<UserDto> updateUserInfo(@RequestBody @Validated UserUpdateParam userUpdateParam) {
        UserDto userDto = userBiz.updateUserInfo(userUpdateParam);
        return userDto != null ? R.ok("修改成功", userDto) : R.fail("修改失败");
    }


    /**
     * 用户名是否被注册
     *
     * @param userName
     * @return com.olrtc.common.core.domain.R<java.lang.Boolean>
     * @author njy
     * @since 17:18 2023/4/28
     */
    @GetMapping("user/userName")
    public R<Boolean> userNameHasExist(String userName) {
        log.info("userNameHasExist() called with parameters => [userName = {}]",userName);
        if (StrUtil.isBlank(userName)) {
            return R.ok("ok", false);
        }
        User user = userService.getUserInfoByUserName(userName);
        return R.ok("ok", user != null);
    }


}
