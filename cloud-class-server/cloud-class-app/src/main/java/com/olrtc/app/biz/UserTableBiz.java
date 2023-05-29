package com.olrtc.app.biz;

import com.olrtc.app.model.domain.PieChart;
import com.olrtc.app.model.dto.UserCourseTableDto;
import com.olrtc.app.model.param.UserCourseTableQueryParam;

import java.util.List;

/**
 * @author njy
 * @since 2023/4/18 09:45
 */
public interface UserTableBiz {

    /**
     * 查询用户课程表
     *
     * @param param param
     * @return com.olrtc.app.model.dto.UserCourseTableDto
     * @author njy
     * @since 09:50 2023/4/18
     */
    UserCourseTableDto getUserCourseTable(UserCourseTableQueryParam param);

    List<PieChart> getJoinCourseInfo(Integer userId);

    List<PieChart> getCreateCourseInfo(Integer userId);
}
