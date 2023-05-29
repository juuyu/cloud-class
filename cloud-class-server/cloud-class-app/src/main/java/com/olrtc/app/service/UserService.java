package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.User;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_user】的数据库操作Service
 * @createDate 2023-02-22 10:56:10
 */
public interface UserService extends IService<User> {

    /**
     * 根据账号查询用户
     *
     * @param userName
     * @return com.olrtc.app.model.entity.User
     */
    User getUserInfoByUserName(String userName);

    /**
     * 根据邮箱查找用户
     *
     * @param email
     * @return com.olrtc.app.model.entity.User
     */
    User getUserInfoByEmail(String email);

    /**
     * 查询用户
     *
     * @param queryParam
     * @return java.util.List<com.olrtc.app.model.dto.UserDto>
     * @author njy
     * @since 16:42 2023/3/29
     */
    List<UserDto> queryUser(String queryParam);

    /**
     * 修改用户头像
     *
     * @param userId
     * @param avatarUrl
     * @return com.olrtc.app.model.dto.UserDto
     */
    UserDto changeUserAvatar(Integer userId, String avatarUrl);

}
