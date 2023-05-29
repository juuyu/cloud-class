package com.olrtc.app.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.dao.LiveMapper;
import com.olrtc.app.model.entity.Live;
import com.olrtc.app.service.LiveService;
import org.springframework.stereotype.Service;

/**
 * @author yu
 * @description 针对表【t_live】的数据库操作Service实现
 * @createDate 2023-03-15 17:08:48
 */
@Service
public class LiveServiceImpl extends ServiceImpl<LiveMapper, Live>
        implements LiveService {


    @Override
    public Live getLiveByCourseId(Integer courseId) {
        return getOne(new LambdaQueryWrapper<Live>()
                .eq(Live::getCourseId, courseId)
        );
    }

    @Override
    public Live getLiveByRoomCode(String roomCode) {
        return getOne(new LambdaQueryWrapper<Live>()
                .eq(Live::getRoomId, roomCode)
        );
    }
}




