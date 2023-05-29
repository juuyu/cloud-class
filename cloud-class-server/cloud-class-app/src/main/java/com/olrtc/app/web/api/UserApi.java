package com.olrtc.app.web.api;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.security.domain.LoginUser;
import com.olrtc.common.security.utils.LoginUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/2/20 14:04
 */
@RestController
@RequestMapping("api/user")
public class UserApi {

    @GetMapping
    public R<Boolean> userAuthentication(){
        return R.ok(StpUtil.isLogin());
    }

    @PostMapping
    public R<UserDto> getUserInfoByToken(){
        LoginUser loginUser = LoginUtil.getLoginUser();
        if (loginUser == null){
            return R.fail();
        }
        UserDto userDto = Convert.convert(UserDto.class, loginUser);
        return R.ok(userDto);
    }






}
