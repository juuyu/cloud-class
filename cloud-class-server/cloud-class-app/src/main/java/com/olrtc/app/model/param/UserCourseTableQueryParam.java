package com.olrtc.app.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author njy
 * @since 2023/4/18 09:47
 */
@Data
public class UserCourseTableQueryParam {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date startTime;

    private Integer userId;

}
