package com.olrtc.app.remote.ai.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/3/31 11:45
 */
@Data
@Accessors(chain = true)
public class OpenAiModel {

    private Integer id;

    private String object;

    private Long created;

    private String owned_by;

    private Permission permission;



    private static class Permission {
        private String id;
        private String object;



    }
}
