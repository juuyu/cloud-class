package com.olrtc.app.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.UserMapper;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.service.UserService;
import com.olrtc.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yu
 * @description 针对表【t_user】的数据库操作Service实现
 * @createDate 2023-02-22 10:56:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getUserInfoByUserName(String userName) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
    }

    @Override
    public User getUserInfoByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    @Override
    public List<UserDto> queryUser(String queryParam) {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .like(StringUtils.isNotBlank(queryParam), User::getUserName, queryParam)
                .or()
                .like(StringUtils.isNotBlank(queryParam), User::getRealName, queryParam)

        );
        return Convert.convert(new TypeReference<>() {
        }, users);
    }

    @Override
    public UserDto changeUserAvatar(Integer userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("id=" + userId + "的用户信息不存在");
        }
        user.setAvatar(avatarUrl);
        int i = userMapper.updateById(user);
        return i > 0 ? Convert.convert(UserDto.class, user) : null;
    }


}




