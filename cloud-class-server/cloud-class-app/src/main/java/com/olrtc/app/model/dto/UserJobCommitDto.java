package com.olrtc.app.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/4/9 00:53
 */
@Data
@Accessors(chain = true)
public class UserJobCommitDto {

    private Integer userId;

    private String userName;

    private String realName;
    /**
     * 提交日期
     */
    private String commitTime;

    private String checkInfoTag;
    private String checkInfo;

    private CourseJobCommitDto courseJobCommitDto;

}
