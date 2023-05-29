package com.olrtc.app.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author njy
 * @since 2023/4/9 21:33
 */
@Data
@Accessors(chain = true)
public class CourseJobCheckParam {
    private Integer courseJobCommitId;
    private int score;
    private String comment;
}
