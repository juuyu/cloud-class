package com.olrtc.app.web;

import com.olrtc.app.biz.UserTableBiz;
import com.olrtc.app.model.domain.PieChart;
import com.olrtc.app.model.dto.UserCourseTableDto;
import com.olrtc.app.model.param.UserCourseTableQueryParam;
import com.olrtc.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author njy
 * @since 2023/4/18 09:44
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("table")
public class UserTableController {

    private final UserTableBiz userTableBiz;


    /**
     * 查询用户课程表
     *
     * @param param param
     * @return com.olrtc.common.core.domain.R<com.olrtc.app.model.dto.UserCourseTableDto>
     * @author njy
     * @since 09:49 2023/4/18
     */
    @GetMapping
    public R<UserCourseTableDto> getUserCourseTable(UserCourseTableQueryParam param) {
        UserCourseTableDto userCourseTableDto = userTableBiz.getUserCourseTable(param);
        return R.ok("ok", userCourseTableDto);
    }


    @GetMapping("joinCourse")
    public R<List<PieChart>> getJoinCourseInfo(Integer userId) {
        log.info("getJoinCourseInfo() called with parameters => [userId = {}]",userId);
        List<PieChart> list = userTableBiz.getJoinCourseInfo(userId);
        return R.ok("ok", list);
    }

    @GetMapping("createCourse")
    public R<List<PieChart>> getCreateCourseInfo(Integer userId) {
        log.info("getCreateCourseInfo() called with parameters => [userId = {}]",userId);
        List<PieChart> list = userTableBiz.getCreateCourseInfo(userId);
        return R.ok("ok", list);
    }


}
