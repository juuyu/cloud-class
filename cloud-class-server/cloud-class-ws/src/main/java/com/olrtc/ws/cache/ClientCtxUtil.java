package com.olrtc.ws.cache;

import com.olrtc.common.core.utils.StrUtil;
import com.olrtc.ws.domain.ClientCtxInfo;
import com.olrtc.ws.remote.model.dto.UserDto;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/11 22:01
 */
@Slf4j
@Component
public class ClientCtxUtil {

//    private static RemoteUserService remoteUserService;
//
//
//    @Resource
//    public void setRemoteUserService(RemoteUserService remoteUserService){
//        ClientCtxUtil.remoteUserService = remoteUserService;
//    }



    // 校验 token 的合法性
//    public static CompletableFuture<UserDto> isValidToken(String token) {
//        if (StrUtil.isBlank(token)) {
//            return CompletableFuture.completedFuture(null);
//        }
//        CompletableFuture<UserDto> future = new CompletableFuture<>();
//        remoteUserService.getUserInfoByToken(token).subscribe(
//                r -> {
//                    UserDto userDto = r.getData();
//                    if (r.getCode() == 200 && userDto != null) {
//                        future.complete(userDto);
//                    } else {
//                        future.complete(null);
//                    }
//                },
//                err -> future.complete(null)
//        );
//        return future;
//    }


    /**
     * 用户连接
     *
     * @param userDto userDto
     * @param channel     channel
     */
    public static void userConnect(UserDto userDto, Channel channel) {
        log.info("userConnect() called with parameters => [userDto = {}], [channel = {}]", userDto, channel);
        if (channel != null && userDto != null){
            Attribute<ClientCtxInfo> attr = channel.attr(ClientCtxCache.channelKey);
            attr.set(new ClientCtxInfo(new Date(), userDto));
            ClientCtxCache.INSTANCE.channelMapPut(userDto.getUserName(), channel);
        }
    }


    /**
     * 用户断开连接
     *
     * @param channel channel
     */
    public static void userDisConnect(Channel channel) {
        log.info("userDisConnect() called with parameters => [channel = {}]", channel);
        if (channel != null){
            ClientCtxInfo clientCtxInfo = getClientCtxInfo(channel);
            if (clientCtxInfo != null) {
                UserDto userDto = clientCtxInfo.getUserDto();
                if (userDto != null) {
                    String userName = userDto.getUserName();
                    if (!StrUtil.isBlank(userName)) {
                        ClientCtxCache.INSTANCE.channelMapRemove(userName);
                    }
                }
            }
            channel.writeAndFlush(new CloseWebSocketFrame());
            // ctx.disconnect(); //通道将不再接收到来自远程对等体的数据，但仍可以发送数据
            channel.close(); //方法将导致通道关闭并释放所有相关资源
        }
    }



    public static ClientCtxInfo getClientCtxInfo(Channel channel){
        ClientCtxInfo clientCtxInfo = null;
        if (channel != null){
            Attribute<ClientCtxInfo> attr = channel.attr(ClientCtxCache.channelKey);
            clientCtxInfo = attr.get();
        }
        return clientCtxInfo;
    }

}
