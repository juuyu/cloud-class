package com.olrtc.app.handler;

import com.olrtc.app.utils.TraceUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author njy
 * @since 2023/4/23 13:01
 */
@Component
public class TraceInterceptor implements HandlerInterceptor {

    /**
     * 为请求添加trace_id
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @author njy
     * @since 14:59 2022/9/22
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        String traceId;
        if (request.getHeader("traceId") == null) {
            traceId = TraceUtil.generateTraceId();
        } else {
            traceId = request.getHeader("traceId");
        }
        TraceUtil.setTraceId(traceId);
        return true;
    }
}
