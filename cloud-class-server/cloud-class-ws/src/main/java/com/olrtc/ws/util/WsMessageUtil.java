package com.olrtc.ws.util;

import com.olrtc.common.core.utils.JsonUtil;
import com.olrtc.ws.cache.ClientCtxCache;
import com.olrtc.ws.domain.WebSocketMessage;
import com.olrtc.ws.enums.MessageType;
import com.olrtc.ws.remote.RemoteRoomService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author njy
 * @since 2023/3/23 13:18
 */
@Slf4j
@Component
public class WsMessageUtil {

    private static RemoteRoomService remoteRoomService;

    @Resource
    public void setRemoteRoomService(RemoteRoomService remoteRoomService) {
        WsMessageUtil.remoteRoomService = remoteRoomService;
    }


    /**
     * 生成返回消息
     *
     * @param messageType messageType
     * @param data        data
     */
    public static String genRespData(MessageType messageType, Object data) {
        String dataJson = JsonUtil.obj2String(data);
        return JsonUtil.obj2String(new WebSocketMessage(messageType, dataJson));
    }

    /**
     * 返回消息给所有人
     *
     * @param roomId roomId
     * @param data   data
     */
    public static void sendMsgAll(String roomId, String data) {
        remoteRoomService.getUserNamesByRoomId(roomId).subscribe(
                r -> {
                    if (r.getCode() == 200) {
                        r.getData().forEach(userName ->
                                Thread.startVirtualThread(() ->
                                        sendMsg(userName, data)
                                )
                        );
                    }
                },
                err -> log.error("获取房间用户失败, err:", err)
        );
    }


    /**
     * 发送消息给某人
     *
     * @param userName userName
     * @param data     data
     */
    public static void sendMsg(String userName, String data) {
        Channel channel = ClientCtxCache.INSTANCE.channelMapGet(userName);
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(data));
            log.info("send msg to [{}], msg:[{}]", userName, data);
        }
    }

}
