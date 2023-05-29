package com.olrtc.app.biz.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olrtc.app.biz.MessageBiz;
import com.olrtc.app.config.RedisCacheKey;
import com.olrtc.app.dao.MessageMapper;
import com.olrtc.app.model.domain.MessageDeal;
import com.olrtc.app.model.dto.UserMessageDto;
import com.olrtc.app.model.entity.CourseUser;
import com.olrtc.app.model.entity.Message;
import com.olrtc.app.model.entity.User;
import com.olrtc.app.model.param.MessageQueryParam;
import com.olrtc.app.service.CourseUserService;
import com.olrtc.app.service.MessageService;
import com.olrtc.app.service.UserService;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import com.olrtc.common.redis.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author njy
 * @since 2023/4/10 14:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBizImpl implements MessageBiz {

    private final MessageService    messageService;
    private final CourseUserService courseUserService;
    private final MessageMapper     messageMapper;
    private final UserService       userService;


    @Override
    public boolean getMessageNum(Integer userId) {
        if (userId == null) {
            return false;
        }
        String key = RedisCacheKey.MESSAGE.NEW_MESSAGE + userId;
        Boolean has = RedisUtil.getCacheObject(key);
        if (has != null && has) {
            RedisUtil.deleteObject(key);
            return true;
        }
        return false;
    }

    @Override
    public TableDataInfo<UserMessageDto> listUserMessages(PageQuery pageQuery, MessageQueryParam param) {
        Integer userId = param.getUserId();
        String messageName = param.getMessageName();
        Boolean read = param.getMsgRead();
        if (userId == null) {
            throw new BizException("用户不存在");
        }
        // 清除未读信息
        String key = RedisCacheKey.MESSAGE.NEW_MESSAGE + userId;
        RedisUtil.setCacheObject(key, false);

        LambdaQueryWrapper<Message> lqw = new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiveId, userId)
                .like(StrUtil.isNotBlank(messageName), Message::getMsgName, messageName)
                .eq(read != null, Message::getMsgRead, read);

        Page<Message> messagePage = messageMapper.selectPage(pageQuery.build(), lqw);
        IPage<UserMessageDto> userMessageDtoIPage = messagePage.convert(this::convert);
        return TableDataInfo.build(userMessageDtoIPage);
    }

    @Override
    public void replyMessage(String messageCode, Boolean reply) {
        CompletableFuture.runAsync(() -> {
            log.info("replyMessage() called with parameters => [messageCode = {}], [reply = {}]", messageCode, reply);
            String key = RedisCacheKey.MESSAGE.MESSAGE_REPLY + messageCode;
            MessageDeal messageDeal = RedisUtil.getCacheObject(key);
            log.info("messageDeal = {}", JsonUtil.obj2String(messageDeal));
            if (messageDeal == null) {
                return;
            }
            String paramJson = messageDeal.getParamJson();
            boolean res = false;
            switch (messageDeal.getMessageType()) {
                case INVITE -> {
                    CourseUser courseUser = JsonUtil.string2Obj(paramJson, CourseUser.class);
                    res = courseUserService.stuCheckToJoinCourse(courseUser, reply);
                }

                case APPLY -> {
                    CourseUser courseUser = JsonUtil.string2Obj(paramJson, CourseUser.class);
                    res = courseUserService.teaCheckToJoinCourse(courseUser, reply);
                }
            }
            if (res) {
                RedisUtil.deleteObject(key);
            } else {
                log.error("replyMessage() called with parameters => [messageCode = {}], [reply = {}] 处理失败", messageCode, reply);
            }
        });
    }

    @Override
    public void readMessage(Integer messageId) {
        Message message = messageService.getById(messageId);
        if (message != null) {
            if (!message.getMsgRead()) {
                message.setMsgRead(true);
                messageService.updateById(message);
            }
        }
    }

    @Override
    public void delMessage(Integer messageId) {
        messageService.removeById(messageId);
    }


    private UserMessageDto convert(Message message) {
        User user = userService.getById(message.getSendId());
        UserMessageDto messageDto = Convert.convert(UserMessageDto.class, message);
        messageDto.setSendTime(DateUtil.formatDateTime(message.getSendTime()));
        messageDto.setSendRealName(user != null ? user.getRealName() : "");
        messageDto.setSendUserName(user != null ? user.getUserName() : "");
        return messageDto;
    }
}
