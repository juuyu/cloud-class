package com.olrtc.ws.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * @author njy
 * @since 2023/2/1 15:15
 */
public class NettyUtil {

    /**
     * 获取ip
     * @author njy
     * @since 09:48 2022/12/29
     * @param ctx ctx
     * @return java.lang.String
     */
    public static String getRemoteAddress(ChannelHandlerContext ctx) {
        String socketString = "";
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        if(socketAddress != null){
            socketString = socketAddress.toString().substring(1);
        }
        return socketString;
    }


    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    public static void sendHttpErrResponse(Channel channel, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = channel.writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }







}