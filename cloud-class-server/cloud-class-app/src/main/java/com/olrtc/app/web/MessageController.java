package com.olrtc.app.web;

import com.olrtc.app.biz.MessageBiz;
import com.olrtc.app.model.dto.UserMessageDto;
import com.olrtc.app.model.param.MessageQueryParam;
import com.olrtc.common.core.domain.R;
import com.olrtc.common.core.exception.BizException;
import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author njy
 * @since 2023/3/30 13:26
 */
@Slf4j
@RestController
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageBiz messageBiz;


    /**
     * 轮训查看是否有新消息新消息
     *
     * @param userId userId
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.MessageNumDto>
     * @author njy
     * @since 14:56 2023/4/10
     */
    @GetMapping("query")
    public R<Boolean> getMessageNum(Integer userId) {
        return R.ok("ok", messageBiz.getMessageNum(userId));
    }


    /**
     * 处理回执的信息
     *
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 14:33 2023/4/11
     */
    @GetMapping("reply")
    public R<Void> replyMessage(String messageCode, Boolean reply) {
        log.info("replyMessage() called with parameters => [messageCode = {}], [reply = {}]", messageCode, reply);
        if (StrUtil.isBlank(messageCode)) {
            throw new BizException("messageCode不能为null");
        }
        if (reply == null) {
            throw new BizException("reply不能为空");
        }
        messageBiz.replyMessage(messageCode, reply);
        return R.ok("OK");
    }


    /**
     * 将消息标记未已读
     *
     * @param messageId
     * @return com.olrtc.common.core.domain.R<java.lang.Void>
     * @author njy
     * @since 10:14 2023/4/12
     */
    @GetMapping("read")
    public R<Void> readMessage(Integer messageId) {
        messageBiz.readMessage(messageId);
        return R.ok("");
    }

    /**
     * 分页查询用户信息
     *
     * @param pageQuery
     * @param param
     * @return com.olrtc.common.core.domain.R<com.olrtc.common.mybatis.core.page.TableDataInfo < com.olrtc.app.model.dto.UserMessageDto>>
     * @author njy
     * @since 16:26 2023/4/10
     */
    @GetMapping
    public R<TableDataInfo<UserMessageDto>> listUserMessages(PageQuery pageQuery, MessageQueryParam param) {
        log.info("listUserMessages() called with parameters => [pageQuery = {}], [param = {}]", pageQuery, param);
        TableDataInfo<UserMessageDto> messages = messageBiz.listUserMessages(pageQuery, param);
        return R.ok("查询成功", messages);
    }



    @GetMapping("del")
    public R<Void> delMessage(Integer messageId) {
        log.info("delMessage() called with parameters => [messageId = {}]",messageId);
        messageBiz.delMessage(messageId);
        return R.ok("删除成功");
    }

}
