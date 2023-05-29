package com.olrtc.app.config;

/**
 * @author njy
 * @since 2023/3/13 15:44
 */
public class RedisCacheKey {
    public static class LIVE {
        public static final String ROOM_INFO = "live:room:";
        public static final String ROOM_USER = "set:live:room:user:";
        public static final String RECORD    = "list:live:record:";
    }


    public static class MESSAGE {
        public static final String NEW_MESSAGE = "message:user:";

        public static final String MESSAGE_REPLY = "message:reply:";
    }

    public static class RECORD {
        public static final String RECORD_INFO  = "record:info:";
        public static final String RECORD_CHUNK = "list:record:";

    }

    public static class AUTH{
        public static final String MAIL_AUTH_CODE = "auth:mail:";
    }

}
