package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/4/8 23:47
 */
@Data
@Accessors(chain = true)
public class UserJobCommitInfoDto {

    private long commitNum;

    private long totalNum;

    private long checkNum;

    private long commitPercent;

    private long checkPercent;

}
