package com.olrtc.app.utils;


import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author njy
 * @since 2022/9/22 15:02
 */
public class TraceUtil {

    /**
     * 生成traceID
     * @return java.lang.String
     * @author njy
     * @since 15:03 2022/9/22
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取traceID
     * @return java.lang.String
     * @author njy
     * @since 15:04 2022/9/22
     */
    public static String getTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : generateTraceId();
    }

    /**
     * 设置TraceID
     * @param traceId traceId
     * @author njy
     * @since 15:06 2022/9/22
     */
    public static void setTraceId(String traceId) {
        if (traceId != null) {
            MDC.put("traceId", traceId);
        } else {
            MDC.remove("traceId");
        }
    }
}
