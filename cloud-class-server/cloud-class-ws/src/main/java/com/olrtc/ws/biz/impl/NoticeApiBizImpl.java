package com.olrtc.ws.biz.impl;

import com.olrtc.ws.biz.NoticeApiBiz;
import com.olrtc.ws.domain.msg.CommandRespMsg;
import com.olrtc.ws.enums.MessageType;
import com.olrtc.ws.util.WsMessageUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/27 13:28
 */
@Service
public class NoticeApiBizImpl implements NoticeApiBiz {
    @Override
    public void noticeUserExit(String userName) {
        CommandRespMsg commandRespMsg = new CommandRespMsg()
                .setSendTimeTimestamp(String.valueOf(new Date().getTime()))
                .setCamera(false)
                .setScreen(false)
                .setExit(true);

        String res = WsMessageUtil.genRespData(MessageType.COMMAND, commandRespMsg);
        WsMessageUtil.sendMsg(userName,res);
    }

}
