package com.olrtc.app.utils;

import com.olrtc.common.core.utils.Md5Util;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/25 23:08
 */
public class ExcalidrawUtil {



    private static final String baseUrl = "https://excalidraw.com/#room=%s,%s";

    public static String genRoomWhiteboardUrl(String roomId) {
        String room = Md5Util.MD5Encode(roomId)
                .substring(0, 20);
        long time = new Date().getTime();
        String secret = Md5Util.MD5Encode(String.valueOf(time))
                .substring(0, 22);
        return String.format(baseUrl, room, secret);
    }

    public static void main(String[] args) {
        System.out.println(genRoomWhiteboardUrl("1"));
    }

}
