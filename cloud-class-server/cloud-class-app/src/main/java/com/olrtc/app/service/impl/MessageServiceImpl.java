package com.olrtc.app.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.dao.CourseJobMapper;
import com.olrtc.app.dao.CourseMapper;
import com.olrtc.app.dao.CourseUserMapper;
import com.olrtc.app.dao.MessageMapper;
import com.olrtc.app.enums.MessageType;
import com.olrtc.app.model.domain.LiveRoomInfo;
import com.olrtc.app.model.domain.MessageDeal;
import com.olrtc.app.model.dto.CourseDto;
import com.olrtc.app.model.dto.UserDto;
import com.olrtc.app.model.entity.*;
import com.olrtc.app.service.MessageService;
import com.olrtc.app.service.UserService;
import com.olrtc.app.utils.MessageUtil;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author yu
 * @description 针对表【t_message】的数据库操作Service实现
 * @createDate 2023-03-30 13:10:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    private final MessageMapper    messageMapper;
    private final UserService      userService;
    private final CourseUserMapper courseUserMapper;
    private final CourseMapper     courseMapper;
    private final CourseJobMapper  courseJobMapper;


    @Override
    public CompletableFuture<Boolean> sendInviteMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer courseId = courseUser.getCourseId();
                Course course = courseMapper.selectById(courseId);
                User user = userService.getById(course.getTeacherId());

                String inviteMessage = MessageUtil.getInviteMessage(course, user);
                String code = generateReplyCode(new MessageDeal(MessageType.INVITE, JsonUtil.obj2String(courseUser)));
                Message message = getMessage(user.getId(), courseUser.getStudentId(),
                        "邀请加入课程通知", inviteMessage,
                        code);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送邀请信息错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendApplyMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer courseId = courseUser.getCourseId();
                Course course = courseMapper.selectById(courseId);
                User user = userService.getById(courseUser.getStudentId());


                String applyMessage = MessageUtil.getApplyMessage(course, user);

                String code = generateReplyCode(new MessageDeal(MessageType.APPLY, JsonUtil.obj2String(courseUser)));
                Message message = getMessage(user.getId(), course.getTeacherId(),
                        "申请加入课程通知", applyMessage,
                        code);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送申请信息错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendAllowUserJoinMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer courseId = courseUser.getCourseId();
                Course course = courseMapper.selectById(courseId);
                User user = userService.getById(course.getTeacherId());

                String allowUserJoinMessage = MessageUtil.getAllowUserJoinMessage(course, user);
                Message message = getMessage(user.getId(), courseUser.getStudentId(),
                        "加入课程成功通知", allowUserJoinMessage);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送加入课程成功通知错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendRefuseUserJoinMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Integer courseId = courseUser.getCourseId();
                Course course = courseMapper.selectById(courseId);
                User user = userService.getById(course.getTeacherId());

                String refuseUserJoinMessage = MessageUtil.getRefuseUserJoinMessage(course, user);
                Message message = getMessage(user.getId(), courseUser.getStudentId(),
                        "加入课程拒绝通知", refuseUserJoinMessage);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送加入课程拒绝通知错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendUserJoinMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Course course = courseMapper.selectById(courseUser.getCourseId());
                User user = userService.getById(courseUser.getStudentId());


                String userJoinMessage = MessageUtil.userJoinMessage(course, user);
                Message message = getMessage(user.getId(), course.getTeacherId(),
                        "用户同意加入课程通知", userJoinMessage);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送用户同意加入课程通知错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendUserRefuseMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Course course = courseMapper.selectById(courseUser.getCourseId());
                User user = userService.getById(courseUser.getStudentId());


                String userJoinMessage = MessageUtil.userRefuseMessage(course, user);
                Message message = getMessage(user.getId(), course.getTeacherId(),
                        "用户拒绝加入课程通知", userJoinMessage);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送用户拒绝加入课程通知错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendKickUserMessage(CourseUser courseUser) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Course course = courseMapper.selectById(courseUser.getCourseId());
                User user = userService.getById(course.getTeacherId());

                String kickUserMessage = MessageUtil.getKickUserMessage(course, user);
                Message message = getMessage(user.getId(), courseUser.getStudentId(),
                        "被移出课程通知", kickUserMessage);

                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送被移出课程通知通知错误,courseUser:{}", JsonUtil.obj2String(courseUser), e);
                return false;
            }
        });
    }

    private static final String linkFormat = "http://localhost/#/live/stu/class?courseId=%s&courseCode=%s&roomId=%s";

    @Override
    public CompletableFuture<Boolean> liveNoticeMessage(LiveRoomInfo liveRoomInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserDto teacherInfo = liveRoomInfo.getTeacherInfo();
                CourseDto courseDto = liveRoomInfo.getCourseDto();
                Live liveInfo = liveRoomInfo.getLiveInfo();
                String link = String.format(linkFormat, courseDto.getId().toString(),
                        courseDto.getCourseCode(),
                        liveInfo.getId().toString());

                String liveNoticeMessage = MessageUtil.liveNoticeMessage(teacherInfo.getRealName(),
                        teacherInfo.getUserName(),
                        courseDto.getCourseName(),
                        link);

                List<CourseUser> courseUsers = courseUserMapper.selectList(new LambdaQueryWrapper<CourseUser>()
                        .eq(CourseUser::getCourseId, courseDto.getId()));
                courseUsers.forEach(courseUser -> {
                    if (courseUser.getStatus() == 2) {
                        Thread.startVirtualThread(() -> {
                            Message message = getMessage(teacherInfo.getId(), courseUser.getStudentId(),
                                    "上课提醒", liveNoticeMessage);
                            sendMessage(message);
                        });
                    }
                });
                return true;
            } catch (Exception e) {
                log.error("发送上课提醒通知错误", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendJobNoticeMessage(CourseJob courseJob) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Course course = courseMapper.selectById(courseJob.getCourseId());
                String jobNoticeMessage = MessageUtil.jobNoticeMessage(course.getCourseName(),
                        courseJob.getJobName(),
                        DateUtil.formatDate(courseJob.getEndTime()),
                        "");
                List<CourseUser> courseUsers = courseUserMapper.selectList(new LambdaQueryWrapper<CourseUser>()
                        .eq(CourseUser::getCourseId, course.getId()));
                courseUsers.forEach(courseUser -> {
                    if (courseUser.getStatus() == 2) {
                        Thread.startVirtualThread(() -> {
                            Message message = getMessage(course.getTeacherId(), courseUser.getStudentId(),
                                    "新作业发布提醒", jobNoticeMessage);
                            sendMessage(message);
                        });
                    }
                });
                return true;
            } catch (Exception e) {
                log.error("发送作业发布提醒通知错误", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> sendJobCheckMessage(CourseJobCommit courseJobCommit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CourseJob courseJob = courseJobMapper.selectById(courseJobCommit.getCourseJobId());
                Course course = courseMapper.selectById(courseJob.getCourseId());
                String jobCheckMessage = MessageUtil.jobCheckMessage(course.getCourseName(),
                        courseJob.getJobName(),
                        DateUtil.formatDate(courseJobCommit.getCommitTime()),
                        courseJobCommit.getScore().toString(),
                        courseJobCommit.getComment(),
                        courseJobCommit.getSummaryLink()
                );
                Message message = getMessage(course.getTeacherId(), courseJobCommit.getStudentId(),
                        "作业批改完成提醒", jobCheckMessage);
                return sendMessage(message);
            } catch (Exception e) {
                log.error("发送作业批改完成提醒通知错误", e);
                return false;
            }
        });
    }


    private String generateReplyCode(MessageDeal messageDeal) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        RedisUtil.setCacheObject(RedisCacheKey.MESSAGE.MESSAGE_REPLY + uuid, messageDeal);
        return uuid;
    }

    private boolean sendMessage(Message message) {
        int insert = messageMapper.insert(message);
        if (insert > 0) {
            String key = RedisCacheKey.MESSAGE.NEW_MESSAGE + message.getReceiveId();
            RedisUtil.setCacheObject(key, true);
        }
        return insert > 0;
    }


    private static Message getMessage(Integer sendId,
                                      Integer receiveId,
                                      String messageName,
                                      String messageContent) {
        return new Message()
                .setSendId(sendId)
                .setReceiveId(receiveId)
                .setMsgRead(false)
                .setSendTime(new Date())
                .setMsgName(messageName)
                .setMsgContent(messageContent)
                .setNeedReply(false);
    }

    private static Message getMessage(Integer sendId,
                                      Integer receiveId,
                                      String messageName,
                                      String messageContent,
                                      String confirmUrl) {
        return new Message()
                .setSendId(sendId)
                .setReceiveId(receiveId)
                .setMsgRead(false)
                .setSendTime(new Date())
                .setMsgName(messageName)
                .setMsgContent(messageContent)
                .setNeedReply(true)
                .setConfirmUrl(confirmUrl);
    }

}




