package com.olrtc.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.entity.CourseJob;
import com.olrtc.app.model.entity.CourseJobCommit;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.entity.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author yu
 * @description 针对表【t_message】的数据库操作Service
 * @createDate 2023-03-30 13:10:45
 */
public interface MessageService extends IService<Message> {

    CompletableFuture<Boolean> sendInviteMessage(CourseUser courseUser);

    CompletableFuture<Boolean> sendApplyMessage(CourseUser courseUser);

    CompletableFuture<Boolean> sendAllowUserJoinMessage(CourseUser courseUser);

    CompletableFuture<Boolean> sendRefuseUserJoinMessage(CourseUser courseUser);

    CompletableFuture<Boolean> sendUserJoinMessage(CourseUser courseUser);

    CompletableFuture<Boolean> sendUserRefuseMessage(CourseUser courseUser);

    CompletableFuture<Boolean>  sendKickUserMessage(CourseUser courseUser);

    CompletableFuture<Boolean>  liveNoticeMessage(LiveRoomInfo liveRoomInfo);

    CompletableFuture<Boolean>  sendJobNoticeMessage(CourseJob courseJob);

    CompletableFuture<Boolean>  sendJobCheckMessage(CourseJobCommit courseJobCommit);
}
