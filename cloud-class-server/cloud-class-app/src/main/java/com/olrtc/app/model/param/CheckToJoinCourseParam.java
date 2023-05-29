package com.olrtc.app.model.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author njy
 * @since 2023/3/10 11:26
 */
@Data
public class CheckToJoinCourseParam {

    @NotNull(message = "")
    private Integer courseId;

    @NotNull(message = "")
    private Integer userId;

    @NotNull(message = "")
    private Boolean checkRes;


}
