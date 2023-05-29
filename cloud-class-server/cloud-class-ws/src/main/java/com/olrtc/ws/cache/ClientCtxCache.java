package com.olrtc.ws.cache;

import com.olrtc.ws.domain.ClientCtxInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njy
 * @since 2023/3/10 17:47
 */
@Slf4j
public enum ClientCtxCache {

    INSTANCE;

    public static final  AttributeKey<ClientCtxInfo>        channelKey = AttributeKey.newInstance("clientCtxInfo");
    private static final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();



    public void channelMapPut(String userName, Channel channel) {
        channelMap.put(userName, channel);
    }


    public Channel channelMapGet(String userName) {
        return channelMap.get(userName);
    }

    public void channelMapRemove(String userName) {
        channelMap.remove(userName);
    }


}
