package com.olrtc.app.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author njy
 * @since 2023/4/3 17:44
 */
@Data
public class AiCheckCourseJobRes {

    private Boolean success;
    private String  msg;
    private int     score;
    private String  comment;

}
