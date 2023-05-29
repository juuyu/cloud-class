package com.olrtc.ws.domain;

import com.olrtc.ws.remote.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/3/10 17:50
 */
@Data
@AllArgsConstructor
public class ClientCtxInfo {

    private Date onlineTime;

//    private ChannelHandlerContext ctx;

    private UserDto userDto;

}
