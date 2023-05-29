package com.olrtc.ws.biz.impl;

import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.ws.biz.MsgPersistenceBiz;
import com.olrtc.ws.biz.TextWebSocketFrameBiz;
import com.olrtc.ws.cache.ClientCtxCache;
import com.olrtc.ws.domain.ClientCtxInfo;
import com.olrtc.ws.domain.msg.*;
import com.olrtc.ws.enums.MessageType;
import com.olrtc.ws.remote.RemoteRoomService;
import com.olrtc.ws.remote.model.dto.UserDto;
import com.olrtc.ws.util.WsMessageUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author njy
 * @since 2023/3/13 10:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TextWebSocketFrameBizImpl implements TextWebSocketFrameBiz {

    private final RemoteRoomService remoteRoomService;
    private final MsgPersistenceBiz msgPersistenceBiz;

    @Override
    public void handlerChatMessage(ClientCtxInfo clientCtxInfo, ChatSendMessage chatSendMessage) {

        UserDto userDto = clientCtxInfo.getUserDto();
        ChatRespMessage chatRespMessage = new ChatRespMessage()
                .setSendUserName(userDto.getUserName())
                .setSendUserRealName(userDto.getRealName())
                .setAvatar(userDto.getAvatar())
                .setSendTimeTimestamp(chatSendMessage.getSendTimeTimestamp())
                .setMsg(chatSendMessage.getMsg());

        String resp = JsonUtil.obj2String(chatRespMessage);
        TextWebSocketFrame respMsg = new TextWebSocketFrame(resp);

        //群发消息
        if (chatSendMessage.isAtAll()) {
            String roomId = chatSendMessage.getRoomId();
            remoteRoomService.getUserNamesByRoomId(roomId).subscribe(
                    r -> {
                        if (r.getCode() == 200) {
                            r.getData().stream()
                                    .filter(userName -> !userDto.getUserName().equals(userName))
                                    .forEach(userName ->
                                            Thread.startVirtualThread(
                                                    () -> {
                                                        Channel channel = ClientCtxCache.INSTANCE.channelMapGet(userName);
                                                        if (channel != null) {
                                                            channel.writeAndFlush(respMsg);
                                                        }
                                                    }
                                            )
                                    );
                        }
                    },
                    err -> log.error("获取房间用户失败, err:", err)
            );

        } else {
            // 私发
            String toUserName = chatSendMessage.getToUserName();
            Channel channel = ClientCtxCache.INSTANCE.channelMapGet(toUserName);
            if (channel != null) {
                channel.writeAndFlush(respMsg);
            }
        }


    }

    @Override
    public void handlerSdpMessage(ClientCtxInfo clientCtxInfo, SdpSendMessage sdpSendMessage) {

    }

    @Override
    public void handlerChatRoomMessage(ClientCtxInfo clientCtxInfo, ChatSendMessage chatSendMessage) {
        UserDto userDto = clientCtxInfo.getUserDto();
        ChatRespMessage chatRespMessage = new ChatRespMessage();
        if (chatSendMessage.isAtAll()) {
            chatRespMessage.setSendUserName(userDto.getUserName())
                    .setSendUserRealName(userDto.getRealName())
                    .setAvatar(userDto.getAvatar())
                    .setSendTimeTimestamp(chatSendMessage.getSendTimeTimestamp())
                    .setMsg(chatSendMessage.getMsg());
        } else {
            if ("111111".equals(chatSendMessage.getToUserName())) {
                chatRespMessage.setSendUserName("111111")
                        .setSendUserRealName("群聊小助手")
                        .setAvatar("https://oss.lamaro.cn/imgs/robot_01.png")
                        .setSendTimeTimestamp(chatSendMessage.getSendTimeTimestamp())
                        .setMsg(chatSendMessage.getMsg());
            }
        }
        msgPersistenceBiz.recordChatRoomMessage(chatSendMessage, chatRespMessage);
        String res = WsMessageUtil.genRespData(MessageType.CHEAT_ROOM, chatRespMessage);
        WsMessageUtil.sendMsgAll(chatSendMessage.getRoomId(), res);
    }

    @Override
    public void handlerCommandMessage(ClientCtxInfo clientCtxInfo, CommandSendMsg commandSendMsg) {
//        UserDto userDto = clientCtxInfo.getUserDto();
        CommandRespMsg commandRespMsg = new CommandRespMsg()
                .setSendTimeTimestamp(commandSendMsg.getSendTimeTimestamp())
                .setCamera(commandSendMsg.isCamera())
                .setScreen(commandSendMsg.isScreen())
                .setExit(commandSendMsg.isExit());

        String roomId = commandSendMsg.getRoomId();
        try {
            remoteRoomService.updateLiveStatus(roomId, commandRespMsg.isCamera(), commandRespMsg.isScreen()).subscribe(
                    r -> {
                        if (r.getCode() == 200) {
                            log.info("更新房间直播状态成功");
                        }
                    },
                    err -> log.error("获取房间用户失败, err:", err));
        } catch (Exception e) {
            log.error("请求错误", e);
        }

        String res = WsMessageUtil.genRespData(MessageType.COMMAND, commandRespMsg);
        WsMessageUtil.sendMsgAll(roomId, res);
    }
}
