package com.olrtc.ws.remote;


import com.olrtc.common.core.domain.R;
import com.olrtc.ws.remote.model.dto.UserDto;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 用户信息远程调用
 *
 * @author njy
 * @since 2023/2/20 13:40
 */
@HttpExchange("/user")
public interface RemoteUserService {


    /**
     * 用户权限认证
     *
     * @param token token
     * @return reactor.core.publisher.Flux<com.olrtc.common.core.domain.R < java.lang.Boolean>>
     */
    @GetExchange
    R<Boolean> userAuthentication(@RequestHeader("token") String token);

    /**
     * 根据token获取用户信息
     *
     * @param token token
     * @return reactor.core.publisher.Flux<com.olrtc.common.core.domain.R < com.olrtc.ws.remote.domain.UserDto>>
     */
    @PostExchange
    R<UserDto> getUserInfoByToken(@RequestHeader("token") String token);


}
