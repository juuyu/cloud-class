package com.olrtc.app.handler;

import com.olrtc.common.mybatis.handler.MetaObjectHandlerUtil;
import com.olrtc.common.security.utils.LoginUtil;
import org.springframework.stereotype.Component;

/**
 * @author njy
 * @since 2023/2/22 14:16
 */
@Component
public class MyMetaObjectHandlerUtil implements MetaObjectHandlerUtil {
    @Override
    public Integer getUserIdOrDefault() {
        return LoginUtil.getUserIdOrDefault();
    }
}
