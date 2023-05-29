package com.olrtc.app.model.param;

import lombok.Data;

/**
 * @author njy
 * @since 2023/4/21 17:45
 */
@Data
public class RecordQueryParam {

    private Integer userId;

    private Integer recordTime;
    private String  videoName;

    private Boolean open;


}
