package com.olrtc.app.model.param;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/10 16:35
 */
@Data
public class MessageQueryParam {
    private Integer userId;

    private Boolean msgRead;

    private String messageName;
}
