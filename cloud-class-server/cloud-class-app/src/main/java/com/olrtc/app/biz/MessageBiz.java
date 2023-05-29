package com.olrtc.app.biz;

import com.olrtc.app.model.dto.UserMessageDto;
import com.olrtc.app.model.param.MessageQueryParam;
import com.olrtc.common.mybatis.core.page.PageQuery;
import com.olrtc.common.mybatis.core.page.TableDataInfo;

/**
 * @author njy
 * @since 2023/4/10 14:55
 */
public interface MessageBiz {

    /**
     * 查看用户是否有新消息
     *
     * @param userId
     * @return boolean
     * @author njy
     * @since 14:59 2023/4/10
     */
    boolean getMessageNum(Integer userId);

    /**
     * 分页查询用户信息
     *
     * @param pageQuery
     * @param param
     * @return com.olrtc.common.mybatis.core.page.TableDataInfo<com.olrtc.app.model.dto.UserMessageDto>
     * @author njy
     * @since 16:26 2023/4/10
     */
    TableDataInfo<UserMessageDto> listUserMessages(PageQuery pageQuery, MessageQueryParam param);


    void replyMessage(String messageCode, Boolean reply);

    void readMessage(Integer messageId);

    void delMessage(Integer messageId);
}
